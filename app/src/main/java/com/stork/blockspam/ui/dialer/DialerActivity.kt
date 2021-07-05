package com.stork.blockspam.ui.dialer

import android.annotation.SuppressLint
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.stork.blockspam.R
import com.stork.blockspam.extension.showToast
import com.stork.blockspam.extension.telecomManager
import com.stork.blockspam.utils.AppSettingsManager
import com.stork.blockspam.utils.AppSettingsManager.ROLE_CALL_DIAL_ID
import com.stork.blockspam.utils.AppSettingsManager.isDefaultDialer


class DialerActivity : AppCompatActivity() {
    private var callNumber: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.action == Intent.ACTION_CALL && intent.data != null) {
            callNumber = intent.data

            // make sure Simple Dialer is the default Phone app before initiating an outgoing call
            if (!isDefaultDialer(this)) {
                launchSetDefaultDialerIntent()
            } else {
                initOutgoingCall()
            }
        } else {
           showToast("unknown_error_occurred")
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        updateMenuItemColors(menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("MissingPermission")
    private fun initOutgoingCall() {
        try {
            getHandleToUse(intent, callNumber.toString()) { handle ->
                if (handle != null) {
                    Bundle().apply {
                        putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, handle)
                        putBoolean(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE, false)
                        putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, false)
                        telecomManager.placeCall(callNumber, this)
                    }
                }
                finish()
            }
        } catch (e: Exception) {

            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == AppSettingsManager.ROLE_CALL_DIAL_ID) {
            if (!isDefaultDialer(this)) {
                finish()
            } else {
                initOutgoingCall()
            }
        }
    }




    fun launchSetDefaultDialerIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager!!.isRoleAvailable(RoleManager.ROLE_DIALER) && !roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                startActivityForResult(intent, ROLE_CALL_DIAL_ID)
            }
        } else {
            Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName).apply {
                try {
                    startActivityForResult(this, ROLE_CALL_DIAL_ID)
                } catch (e: ActivityNotFoundException) {
                    showToast(getString(R.string.no_app_found))
                } catch (e: Exception) {
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getHandleToUse(intent: Intent?, phoneNumber: String, callback: (handle: PhoneAccountHandle?) -> Unit) {
//        handlePermission(PERMISSION_READ_PHONE_STATE) {
//            if (it) {
        val defaultHandle = telecomManager.getDefaultOutgoingPhoneAccount(PhoneAccount.SCHEME_TEL)
        when {
            intent?.hasExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE) == true -> callback(intent.getParcelableExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE)!!)

            defaultHandle != null -> callback(defaultHandle)
            else -> {
                callback(null)
            }
        }
//            }
//        }
    }
}
