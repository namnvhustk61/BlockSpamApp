package com.stork.blockspam.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService
import android.widget.RemoteViews
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

            /*
           *  Show Screen for Calling
           * */

            // check phone in Locked Screen
            val keyguardService = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val isLocked = keyguardService.inKeyguardRestrictedInputMode()

            // show fullscreen or notification
            val isFullScreen = isLocked || isForeground("com.stork.blockspam") || CallManager.getState() == Call.STATE_CONNECTING

            if(isFullScreen){
                val intent = Intent(this, CallingIncomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }else{
                setupNotification(phone!!)
            }
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

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            if(state != Call.STATE_DIALING){
                updateCallState(state)
            }
        }
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
            if(callContact != null){
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
        val channelId = "simple_dialer_call"
        if (isOreoPlus()) {
            /*
           * if calling is dismiss
           *   PRIORITY_LOW ---> notification not show front
           * */
            val importance = if (callState != Call.STATE_DISCONNECTED) {
                NotificationManager.IMPORTANCE_MAX
            }else{
                NotificationManager.IMPORTANCE_LOW
            }
            // set notification
            val name = "call_notification_channel"
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
            Call.STATE_DIALING -> R.string.dialing
            Call.STATE_DISCONNECTED -> R.string.call_ended
            Call.STATE_DISCONNECTING -> R.string.call_ending
            else -> R.string.ongoing_call
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


        val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.icon_app)
                .setContentIntent(openAppPendingIntent)
                .setPriority(
                        /*
                        * if calling is dismiss
                        *   PRIORITY_LOW ---> notification not show front
                        * */
                        if (callState != Call.STATE_DISCONNECTED) {
                            NotificationCompat.PRIORITY_MAX
                        }else{
                            NotificationCompat.PRIORITY_LOW
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
