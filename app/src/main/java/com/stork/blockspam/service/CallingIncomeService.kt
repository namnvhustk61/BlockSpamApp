package com.stork.blockspam.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.telecom.Call
import android.telecom.InCallService
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.app.NotificationCompat
import com.stork.blockspam.R
import com.stork.blockspam.database.model.CallPhone.CallPhone
import com.stork.blockspam.extension.notificationManager
import com.stork.blockspam.extension.setText
import com.stork.blockspam.extension.setVisibleIf
import com.stork.blockspam.model.PhoneContact
import com.stork.blockspam.storage.ACCEPT_CALL
import com.stork.blockspam.storage.ACTION_BLOCK
import com.stork.blockspam.storage.CALL_BACK
import com.stork.blockspam.storage.DECLINE_CALL
import com.stork.blockspam.ui.MainActivity
import com.stork.blockspam.ui.callingincome.CallManager
import com.stork.blockspam.ui.callingincome.CallingIncomeActivity
import com.stork.blockspam.utils.IntentAction
import com.stork.blockspam.utils.isOreoPlus


class CallingIncomeService : InCallService() {

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)

        getCallPhone{phone->
            /*
            * Check  is Block ?
            * */
            if(CallPhone.isBlockDB(this, phone?:"")){
                CallManager.reject()
                return@getCallPhone
            }

            // show fullscreen or notification , when calling coming
            // usually is  Call.STATE_RINGING
            callCallback.onStateChanged(call, CallManager.getState())
        }

        CallManager.call = call
        CallManager.inCallService = this
        CallManager.registerCallback(callCallback)
    }

    fun isForeground(myPackage: String): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfo = manager.getRunningTasks(1)
        val componentInfo = runningTaskInfo[0].topActivity
        return componentInfo!!.packageName == myPackage
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        CallManager.call = null
        CallManager.inCallService = null
    }

    private fun updateCallState(state: Int) {
        val phone = getCallPhone()
        setupNotification(phone)
    }

    private var statusIconCall:Int = 0
    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)

            when (state) {
                Call.STATE_DIALING -> {
                    statusIconCall = Call.STATE_DIALING// 1
                    openFullScreen()
                    return
                }

                Call.STATE_RINGING -> {
                    statusIconCall = Call.STATE_RINGING // 2
                    // check phone in Locked Screen
                    val keyguardService = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    val isLocked = keyguardService.inKeyguardRestrictedInputMode()
                    val isAppShowing = isForeground("com.stork.blockspam")

                    val isFullScreen = isLocked || isAppShowing
                    if (isFullScreen) {
                        openFullScreen()
                        return
                    }
                }
                Call.STATE_HOLDING, Call.STATE_ACTIVE->{
                    statusIconCall *= 10 // x2
                    return
                }
                Call.STATE_DISCONNECTED->{
                    statusIconCall *= 10 // x3
                }
                Call.STATE_CONNECTING->{
                    // when start call in app --> no show notification
                    return
                }
            }

            updateCallState(state)
        }
    }

    private fun openFullScreen(){
        val intent = Intent(baseContext, CallingIncomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


    private var callContact: PhoneContact? = null
    private fun getCallPhone(): String{
        if(callContact == null){
            CallManager.getCallContact(applicationContext) { contact ->
                callContact = contact
            }
        }

        if(callContact != null){
           return callContact!!.phoneNumbers[0]
        }else{
            return ""
        }
    }

    private fun getCallPhone(callback: (String?) -> Unit){
        CallManager.getCallContact(applicationContext) { contact ->
            callContact = contact
            if(callContact != null && callContact?.phoneNumbers != null && callContact?.phoneNumbers!!.size >0){
                callback.invoke(callContact!!.phoneNumbers[0])
            }else{
                callback(null)
            }
        }
    }

    private fun getCallName(): String?{
        if(callContact == null){
            CallManager.getCallContact(applicationContext) { contact ->
                callContact = contact
            }
        }

       return if (callContact != null && callContact!!.name.isNotEmpty()) callContact!!.name
       else null
    }


    private fun setupNotification(phone: String) {

        CallManager.getCallContact(applicationContext) { contact ->
            callContact = contact
        }
        val callState = CallManager.getState()
        var channelId = "simple_dialer_call"
        var name = ""
        if (isOreoPlus()) {
            /*
           * if calling is dismiss
           *   PRIORITY_LOW ---> notification not show front
           * */
            val importance = if (callState != Call.STATE_DISCONNECTED) {
                channelId = "id_channel_max"
                name = "STATE_CALL_COMING"
                NotificationManager.IMPORTANCE_MAX
            }else{
                channelId = "id_channel_low"
                name = "STATE_CALL_FINISH"
                NotificationManager.IMPORTANCE_LOW
            }
            // set notification

            NotificationChannel(channelId, name, importance).apply {
                setSound(null, null)
                notificationManager.createNotificationChannel(this)
            }
        }

         val activityIntent =
                 if(callState == Call.STATE_DISCONNECTED)
                     MainActivity::class.java
                 else CallingIncomeActivity::class.java


        val callerName = getCallName()?:phone

        // Open App when click  notification
        val openAppIntent = Intent(this, activityIntent)
        openAppIntent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
        val openAppPendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, 0)

        //  click  ACCEPT_CALL
        val acceptCallIntent = Intent(this, BlockCallBroadcastReceiver::class.java)
        acceptCallIntent.action = ACCEPT_CALL
        val acceptPendingIntent = PendingIntent.getBroadcast(this, 0, acceptCallIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        //  click   DECLINE_CALL
        val declineCallIntent = Intent(this, BlockCallBroadcastReceiver::class.java)
        declineCallIntent.action = DECLINE_CALL
        val declinePendingIntent = PendingIntent.getBroadcast(this, 1, declineCallIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        // click action block
        val actionBlockIntent = Intent(this, BlockCallBroadcastReceiver::class.java)
        actionBlockIntent.action = ACTION_BLOCK
        actionBlockIntent.putExtra("phone", phone)
        actionBlockIntent.putExtra("name", callerName)

        val sendBlockPendingIntent = PendingIntent.getBroadcast(this, 2, declineCallIntent, PendingIntent.FLAG_CANCEL_CURRENT)


        // click action call back
        val callBackIntent = Intent(this, BlockCallBroadcastReceiver::class.java)
        callBackIntent.action = CALL_BACK
        callBackIntent.putExtra("phone", phone)
        callBackIntent.putExtra("name", callerName)

        val callBackPendingIntent = PendingIntent.getBroadcast(this, 3, declineCallIntent, PendingIntent.FLAG_CANCEL_CURRENT)


        val contentTextId = when (callState) {
            Call.STATE_RINGING -> R.string.is_calling
            Call.STATE_DISCONNECTED -> R.string.call_ended
            Call.STATE_DISCONNECTING -> R.string.call_ending
            else -> R.string.unknown
        }

        val collapsedView = RemoteViews(packageName, R.layout.call_notification).apply {
            setText(R.id.notification_caller_name,
                    if(callerName!=getString(R.string.unknown_caller))callerName else phone)
            setText(R.id.notification_call_status, getString(contentTextId))
            setVisibleIf(R.id.notification_accept_call, callState == Call.STATE_RINGING)

            if(callState == Call.STATE_DISCONNECTED){
                setVisibleIf(R.id.action_when_end, true)
                setVisibleIf(R.id.notification_actions_holder, false)

            }
            setOnClickPendingIntent(R.id.notification_decline_call, declinePendingIntent)
            setOnClickPendingIntent(R.id.notification_accept_call, acceptPendingIntent)

            setOnClickPendingIntent(R.id.tvBlock, sendBlockPendingIntent)
            setOnClickPendingIntent(R.id.tvCallback, callBackPendingIntent)

            setOnClickFillInIntent(R.id.tvMessage, IntentAction.getIntentSendSMS(phone))

        }

        @ColorInt
        var colorIcon = Color.WHITE
        val rIcon =  when(statusIconCall){

            Call.STATE_DIALING*10->{
                colorIcon = Color.RED
                R.drawable.ic_call_missed_outgoing
            }
            Call.STATE_DIALING*100->{
                colorIcon = Color.GREEN
                R.drawable.ic_call_made_succes
            }


            Call.STATE_RINGING*10->{
                colorIcon = Color.RED
                R.drawable.ic_call_missed_24
            }
            Call.STATE_RINGING*100->{
                colorIcon = Color.BLUE
                R.drawable.ic_call_received_24
            }

            else -> R.mipmap.icon_app
        }


        val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(rIcon)
                .setColor(colorIcon)
                .setContentIntent(openAppPendingIntent)
                .setPriority(
                        /*
                        * if calling is dismiss
                        *   PRIORITY_LOW ---> notification not show front
                        * */
                        if (callState != Call.STATE_DISCONNECTED) {
                            NotificationCompat.PRIORITY_MAX
                        }else{
                            NotificationCompat.PRIORITY_DEFAULT
                        }
                )
                .setCategory(Notification.CATEGORY_CALL)
                .setCustomContentView(collapsedView)
                .setOngoing(callState != Call.STATE_DISCONNECTED)
//                .setSound(null)
                .setUsesChronometer(callState == Call.STATE_ACTIVE)
                .setChannelId(channelId)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())

        val notification = builder.build()
        notificationManager.notify(1, notification)
    }
}
