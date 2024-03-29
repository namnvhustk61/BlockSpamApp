package com.stork.blockspam.ui.callingincome

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.AudioManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.telecom.Call
import android.telecom.CallAudioState
import android.util.Size
import android.view.View
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.stork.blockspam.R
import com.stork.blockspam.base.BaseActivity
import com.stork.blockspam.extension.*
import com.stork.blockspam.model.PhoneContact
import com.stork.blockspam.service.BlockCallBroadcastReceiver
import com.stork.blockspam.storage.ACCEPT_CALL
import com.stork.blockspam.storage.DECLINE_CALL
import com.stork.blockspam.utils.isOreoMr1Plus
import com.stork.blockspam.utils.isOreoPlus
import kotlinx.android.synthetic.main.activity_calling_income.*
import kotlinx.android.synthetic.main.layout_dialpad.*
import java.util.*


class CallingIncomeActivity : BaseActivity() {
    private val CALL_NOTIFICATION_ID = 1

    private var isSpeakerOn = false
    private var isMicrophoneOn = true
    private var isCallEnded = false
    private var callDuration = 0
    private var callContact: PhoneContact? = null
    private var callContactAvatar: Bitmap? = null
    private var proximityWakeLock: PowerManager.WakeLock? = null
    private var callTimer = Timer()

    private var phoneNumber: String = ""
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling_income)
        initButtons()
        audioManager.mode = AudioManager.MODE_IN_CALL
        CallManager.getCallContact(applicationContext) { contact ->
            callContact = contact
            callContactAvatar = getCallContactAvatar()
            runOnUiThread {
                setupNotification()
                updateOtherPersonsInfo()
                checkCalledSIMCard()
            }
        }
        addLockScreenFlags()

        CallManager.registerCallback(callCallback)
        updateCallState(CallManager.getState())
    }

    override fun onDestroy() {
        super.onDestroy()
//        notificationManager.cancel(CALL_NOTIFICATION_ID)
        CallManager.unregisterCallback(callCallback)
        callTimer.cancel()
        if (proximityWakeLock?.isHeld == true) {
            proximityWakeLock!!.release()
        }

        endCall()
    }

    override fun onBackPressed() {
        if (dialpad_wrapper.isVisible()) {
            dialpad_wrapper.beGone()
            return
        } else {
            super.onBackPressed()
        }

        if (CallManager.getState() == Call.STATE_DIALING) {
            endCall()
        }
    }

    private fun initButtons() {
        call_decline.setOnClickListener {
            endCall()
        }

        call_accept.setOnClickListener {
            acceptCall()
        }

        call_toggle_microphone.setOnClickListener {
            toggleMicrophone()
        }

        call_toggle_speaker.setOnClickListener {
            toggleSpeaker()
        }

        call_dialpad.setOnClickListener {
            toggleDialpadVisibility()
        }

        dialpad_close.setOnClickListener {
            dialpad_wrapper.beGone()
        }

        call_end.setOnClickListener {
            endCall()
        }

        dialpad_0_holder.setOnClickListener { dialpadPressed('0') }
        dialpad_1.setOnClickListener { dialpadPressed('1') }
        dialpad_2.setOnClickListener { dialpadPressed('2') }
        dialpad_3.setOnClickListener { dialpadPressed('3') }
        dialpad_4.setOnClickListener { dialpadPressed('4') }
        dialpad_5.setOnClickListener { dialpadPressed('5') }
        dialpad_6.setOnClickListener { dialpadPressed('6') }
        dialpad_7.setOnClickListener { dialpadPressed('7') }
        dialpad_8.setOnClickListener { dialpadPressed('8') }
        dialpad_9.setOnClickListener { dialpadPressed('9') }

        dialpad_0_holder.setOnLongClickListener { dialpadPressed('+'); true }
        dialpad_asterisk.setOnClickListener { dialpadPressed('*') }
        dialpad_hashtag.setOnClickListener { dialpadPressed('#') }

        imgActionBellClose.setOnClickListener(object : View.OnClickListener{
            var setMute: Boolean = true
            override fun onClick(v: View?) {
                actionBellClose(setMute)
            }
        })
        imgActionBlock.setOnClickListener {
            blockPhone(phoneNumber, name)
        }

    }

    private fun dialpadPressed(char: Char) {
        CallManager.keypad(char)
        dialpad_input.addCharacter(char)
    }

    private fun toggleSpeaker() {
        isSpeakerOn = !isSpeakerOn
        val drawable = if (isSpeakerOn) R.drawable.ic_speaker_on_vector else R.drawable.ic_speaker_off_vector
        call_toggle_speaker.setImageDrawable(getDrawable(drawable))
        audioManager.isSpeakerphoneOn = isSpeakerOn

        val newRoute = if (isSpeakerOn) CallAudioState.ROUTE_SPEAKER else CallAudioState.ROUTE_EARPIECE
        CallManager.inCallService?.setAudioRoute(newRoute)
    }

    private fun actionBellClose(setMute: Boolean){
        // off Music
        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
        // off Rung
        val pattern = longArrayOf(10000)
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if(v != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(pattern, 0)
            } else {
                //deprecated in API 26
                v.vibrate(pattern, 0)
            }
            v.cancel()
        }

    }

    private fun toggleMicrophone() {
        isMicrophoneOn = !isMicrophoneOn
        val drawable = if (isMicrophoneOn) R.drawable.ic_microphone_vector else R.drawable.ic_microphone_off_vector
        call_toggle_microphone.setImageDrawable(getDrawable(drawable))
        audioManager.isMicrophoneMute = !isMicrophoneOn
        CallManager.inCallService?.setMuted(!isMicrophoneOn)
    }

    private fun toggleDialpadVisibility() {
        if (dialpad_wrapper.isVisible()) {
            dialpad_wrapper.beGone()
        } else {
            dialpad_wrapper.beVisible()
        }
    }

    private fun updateOtherPersonsInfo() {
        if (callContact == null) {
            return
        }

        // set Name Caller

        if (callContact!!.phoneNumbers.isNotEmpty()) {
            caller_number_label.text = callContact!!.phoneNumbers[0]
        }
        if (callContact!!.name.isNotEmpty() && callContact!!.name != caller_number_label.text){
            caller_name_label.text = callContact!!.name
        }  else {
            caller_name_label.text = getText(R.string.unknown_caller)
        }
        name = caller_name_label.text.toString()
        phoneNumber = caller_number_label.text.toString()

        // set Avatar
        if (callContactAvatar != null) {
            caller_avatar_image.setImageBitmap(callContactAvatar)

            caller_avatar_image.visibility = View.VISIBLE
            caller_avatar_text.visibility = View.INVISIBLE
        }else{
            if(caller_name_label.text != getText(R.string.unknown_caller) ){
                caller_avatar_text.background = ContextCompat.getDrawable(this, getRandomBgDrawable())
                caller_avatar_text.text = get2CharHeadOfName(caller_name_label.text.toString())
            }else{
                caller_avatar_text.text = "#"
                caller_avatar_text.background = ContextCompat.getDrawable(this, R.drawable.ic_text_view_round_0)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkCalledSIMCard() {
        try {
            val accounts = telecomManager.callCapablePhoneAccounts
            if (accounts.size > 1) {
                accounts.forEachIndexed { index, account ->
                    if (account == CallManager.call?.details?.accountHandle) {
                        call_sim_id.text = "${index + 1}"
                        call_sim_id.beVisible()
                        call_sim_image.beVisible()
                    }
                }
            }
        } catch (ignored: Exception) {
        }
    }

    private fun updateCallState(state: Int) {
        when (state) {
            Call.STATE_RINGING -> callRinging()
            Call.STATE_ACTIVE -> callStarted()
            Call.STATE_DISCONNECTED -> endCall()
            Call.STATE_CONNECTING, Call.STATE_DIALING -> initOutgoingCallUI()
            Call.STATE_SELECT_PHONE_ACCOUNT -> showPhoneAccountPicker()
        }

        if (state == Call.STATE_DISCONNECTED || state == Call.STATE_DISCONNECTING) {
            callTimer.cancel()
        }

        val statusTextId = when (state) {
            Call.STATE_RINGING -> R.string.is_calling
            Call.STATE_DIALING -> R.string.dialing
            else -> 0
        }

        if (statusTextId != 0) {
            call_status_label.text = getString(statusTextId)
        }

        setupNotification()
    }

    private fun acceptCall() {
        CallManager.accept()
    }

    private fun initOutgoingCallUI() {
        initProximitySensor()
        incoming_call_holder.beGone()
        ongoing_call_holder.beVisible()
    }

    private fun callRinging() {
        incoming_call_holder.beVisible()
    }

    private fun callStarted() {
        initProximitySensor()
        incoming_call_holder.beGone()
        ongoing_call_holder.beVisible()
        try {
            callTimer.scheduleAtFixedRate(getCallTimerUpdateTask(), 1000, 1000)
        } catch (ignored: Exception) {
        }
    }

    private fun showPhoneAccountPicker() {
        if (callContact != null) {
            getHandleToUse(intent, callContact!!.phoneNumbers[0]) { handle ->
                CallManager.call?.phoneAccountSelected(handle, false)
            }
        }
    }

    private fun endCall() {
        CallManager.reject()
        if (proximityWakeLock?.isHeld == true) {
            proximityWakeLock!!.release()
        }

        if (isCallEnded) {
            finish()
            return
        }

        try {
            audioManager.mode = AudioManager.MODE_NORMAL
        } catch (ignored: Exception) {
        }

        isCallEnded = true
        if (callDuration > 0) {
            runOnUiThread {
                call_status_label.text = "${callDuration.getFormattedDuration()} (${getString(R.string.call_ended)})"
                Handler().postDelayed({
                    finish()
                }, 3000)
            }
        } else {
            call_status_label.text = getString(R.string.call_ended)
            finish()
        }
    }

    private fun getCallTimerUpdateTask() = object : TimerTask() {
        override fun run() {
            callDuration++
            runOnUiThread {
                if (!isCallEnded) {
                    call_status_label.text = callDuration.getFormattedDuration()
                }
            }
        }
    }

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            updateCallState(state)
        }
    }

    @SuppressLint("NewApi")
    private fun addLockScreenFlags() {
        if (isOreoMr1Plus()) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }

        if (isOreoPlus()) {
            (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).requestDismissKeyguard(this, null)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }
    }

    private fun initProximitySensor() {
        val MINUTE_SECONDS = 60

        if (proximityWakeLock == null || proximityWakeLock?.isHeld == false) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            proximityWakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "com.simplemobiletools.dialer.pro:wake_lock")
            proximityWakeLock!!.acquire(10 * MINUTE_SECONDS * 1000L)
        }
    }

    @SuppressLint("NewApi")
    private fun setupNotification() {
        return
        val callState = CallManager.getState()
        val channelId = "simple_dialer_call"
        if (isOreoPlus()) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val name = "call_notification_channel"

            NotificationChannel(channelId, name, importance).apply {
                setSound(null, null)
                notificationManager.createNotificationChannel(this)
            }
        }

        val openAppIntent = Intent(this, CallingIncomeActivity::class.java)
        openAppIntent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
        val openAppPendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, 0)

        val acceptCallIntent = Intent(this, BlockCallBroadcastReceiver::class.java)
        acceptCallIntent.action = ACCEPT_CALL
        val acceptPendingIntent = PendingIntent.getBroadcast(this, 0, acceptCallIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val declineCallIntent = Intent(this, BlockCallBroadcastReceiver::class.java)
        declineCallIntent.action = DECLINE_CALL
        val declinePendingIntent = PendingIntent.getBroadcast(this, 1, declineCallIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val callerName = if (callContact != null && callContact!!.name.isNotEmpty()) callContact!!.name else getString(R.string.unknown_caller)
        val contentTextId = when (callState) {
            Call.STATE_RINGING -> R.string.is_calling
            Call.STATE_DIALING -> R.string.dialing
            Call.STATE_DISCONNECTED -> R.string.call_ended
            Call.STATE_DISCONNECTING -> R.string.call_ending
            else -> R.string.ongoing_call
        }

        val collapsedView = RemoteViews(packageName, R.layout.call_notification).apply {
            setText(R.id.notification_caller_name, callerName)
            setText(R.id.notification_call_status, getString(contentTextId))
            setVisibleIf(R.id.notification_accept_call, callState == Call.STATE_RINGING)

            if(callState == Call.STATE_DISCONNECTED){
                setVisibleIf(R.id.action_when_end, true)
                setVisibleIf(R.id.notification_actions_holder, false)

            }
            setOnClickPendingIntent(R.id.notification_decline_call, declinePendingIntent)
            setOnClickPendingIntent(R.id.notification_accept_call, acceptPendingIntent)

            if (callContactAvatar != null) {
                setImageViewBitmap(R.id.notification_thumbnail, getCircularBitmap(callContactAvatar!!))
            }
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.icon_app)
            .setContentIntent(openAppPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(Notification.CATEGORY_CALL)
            .setCustomContentView(collapsedView)
            .setOngoing(callState != Call.STATE_DISCONNECTED)
            .setSound(null)
            .setUsesChronometer(callState == Call.STATE_ACTIVE)
            .setChannelId(channelId)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())

        val notification = builder.build()
        notificationManager.notify(CALL_NOTIFICATION_ID, notification)
    }

    @SuppressLint("NewApi")
    private fun getCallContactAvatar(): Bitmap? {
        var bitmap: Bitmap? = null
        if (callContact?.photoUri?.isNotEmpty() == true) {
            val photoUri = Uri.parse(callContact!!.photoUri)
            try {
                bitmap = if (isQPlus()) {
                    val tmbSize = resources.getDimension(R.dimen.size_icon).toInt()
                    contentResolver.loadThumbnail(photoUri, Size(tmbSize, tmbSize), null)
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, photoUri)

                }

                bitmap = getCircularBitmap(bitmap!!)
            } catch (ignored: Exception) {
                return null
            }
        }

        return bitmap
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.width, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val radius = bitmap.width / 2.toFloat()

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(radius, radius, radius, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }
}