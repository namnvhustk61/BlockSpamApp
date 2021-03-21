package com.stork.blockspam.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import com.android.internal.telephony.ITelephony
import com.stork.blockspam.database.model.CallPhone.CallPhone
import com.stork.blockspam.utils.AppPermission

internal class BlockCallBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!AppPermission.hasPermission(context, AppPermission.PER_READ_PHONE_STATE) ||
                !AppPermission.hasPermission(context, AppPermission.PER_READ_CALL_LOG)) {
            return
        }

        // get telephony service
        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (telephony.callState != TelephonyManager.CALL_STATE_RINGING) {
            return
        }

        // From https://developer.android.com/reference/android/telephony/TelephonyManager:
        // If the receiving app has Manifest.permission.READ_CALL_LOG and
        // Manifest.permission.READ_PHONE_STATE permission, it will receive the broadcast twice;
        // one with the EXTRA_INCOMING_NUMBER populated with the phone number, and another
        // with it blank. Due to the nature of broadcasts, you cannot assume the order in which
        // these broadcasts will arrive, however you are guaranteed to receive two in this case.
        // Apps which are interested in the EXTRA_INCOMING_NUMBER can ignore the broadcasts where
        // EXTRA_INCOMING_NUMBER is not present in the extras (e.g. where Intent#hasExtra(String)
        // returns false).
//        if (!intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
//            return
//        }
        // get incoming call number.
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        if(CallPhone.hasDB(context, number?:"")){
            breakCall(context)
        }
    }


    // Ends phone call
    private fun breakCall(context: Context) {

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.P) {
            breakCallPieAndHigher(context)
        } else {
            breakCallNougatAndLower(context)
        }
    }

    private fun breakCallNougatAndLower(context: Context) {
        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            val c = Class.forName(telephony.javaClass.name)
            val m = c.getDeclaredMethod("getITelephony")
            m.isAccessible = true
            val telephonyService: ITelephony = m.invoke(telephony) as ITelephony
            telephonyService.endCall()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("NewApi")
    private fun breakCallPieAndHigher(context: Context) {
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        try {
            telecomManager.javaClass.getMethod("endCall").invoke(telecomManager)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}