package com.stork.blockspam.extension

import android.annotation.SuppressLint
import android.content.Intent
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.stork.blockspam.utils.AppPermission
import com.stork.blockspam.utils.AppPermission.hasPermission
import com.stork.blockspam.utils.SelectSIMDialog

// used at devices with multiple SIM cards
@SuppressLint("MissingPermission")
fun AppCompatActivity.getHandleToUse(intent: Intent?, phoneNumber: String, callback: (handle: PhoneAccountHandle?) -> Unit) {

    if ( AppPermission.hasPermission(this, AppPermission.PER_READ_PHONE_STATE)) {
        val defaultHandle = telecomManager.getDefaultOutgoingPhoneAccount(PhoneAccount.SCHEME_TEL)
        when {
            intent?.hasExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE) == true -> callback(intent.getParcelableExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE)!!)

            defaultHandle != null -> callback(defaultHandle)
            else -> {
                SelectSIMDialog(this, phoneNumber) { handle ->
                    callback(handle)
                }
            }
        }
    }
}
