package com.stork.blockspam.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import androidx.appcompat.app.AppCompatActivity
import com.stork.blockspam.AppConfig
import com.stork.blockspam.R
import com.stork.blockspam.database.model.CallPhone.CallPhone
import com.stork.blockspam.database.model.CallPhone.CallPhoneKEY
import com.stork.blockspam.extension.showToast
import com.stork.blockspam.extension.telecomManager

object IntentAction {
    fun callPhone(activity: AppCompatActivity, phone: String){
        if (AppSettingsManager.isDefaultDialer(activity)) {
            getHandleToUse(activity, null, phone) { handle ->
                launchCallIntent(activity, phone, handle)
            }
        } else {
            launchCallIntent(activity, phone, null)
        }
    }

    fun intentCallPhoneFromNotification(context: Context, phone: String){
        val intentDial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        intentDial.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        context.startActivity(intentDial)
    }

    fun blockPhone(context: Context, phone: String, name: String){
        val callPhone = CallPhone()
        callPhone.phone = phone
        callPhone.name  = name
        callPhone.type = CallPhoneKEY.TYPE.TYPE_LOCAL
        callPhone.status = CallPhoneKEY.STATUS.STATUS_BLOCK
        val status = callPhone.insertDB(context)
        when(status){
            AppConfig.SUCCESS -> {
                context.showToast(context.getString(R.string.block_successfully))
            }
            AppConfig.ERROR -> {
                context.showToast(context.getString(R.string.all_phone__alert_err_phone_saved))
            }
            AppConfig.EXCEPTION -> {
                context.showToast(context.getString(R.string.all_phone__alert_add_excaeption))
            }
        }
    }

    fun sendSMS(context: Context, phone: String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null))
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        context.startActivity(intent)
    }
     fun sendSMSFromNotification(context: Context, phone: String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null))
        intent.addCategory(Intent.CATEGORY_DEFAULT)
         intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
         context.startActivity(intent)
    }
    fun getIntentSendSMS(phone: String): Intent{
        val intent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null))
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        return intent
    }
    // used at devices with multiple SIM cards
    @SuppressLint("MissingPermission")
    fun getHandleToUse(
        activity: AppCompatActivity,
        intent: Intent?,
        phoneNumber: String,
        callback: (handle: PhoneAccountHandle?) -> Unit
    ) {
        if (AppPermission.hasPermission(activity, AppPermission.PER_READ_PHONE_STATE)) {
            val defaultHandle = activity.telecomManager.getDefaultOutgoingPhoneAccount(PhoneAccount.SCHEME_TEL)
            when {
                intent?.hasExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE) == true -> callback(
                    intent.getParcelableExtra(
                        TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE
                    )!!
                )

                defaultHandle != null -> callback(defaultHandle)
                else -> {
                    SelectSIMDialog(activity, phoneNumber) { handle ->
                        callback(handle)
                    }
                }
            }
        }else{
            AppPermission.requirePermissions(
                activity,
                AppPermission.PER_READ_PHONE_STATE,
                AppPermission.PER_REQUEST_CODE_READ_PHONE
            )
        }
    }

    fun launchCallIntent(
        activity: AppCompatActivity,
        recipient: String,
        handle: PhoneAccountHandle? = null
    ) {
        val action = if (AppPermission.hasPermission(activity, AppPermission.PER_CALL_PHONE)){
            Intent.ACTION_CALL
        }else{Intent.ACTION_DIAL}

        Intent(action).apply {
            data = Uri.fromParts("tel", recipient, null)
            if (handle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, handle)
            }
            try {
                activity.startActivity(this)
            } catch (e: ActivityNotFoundException) {
                activity.showToast(activity.getString(R.string.no_app_found))
            } catch (e: Exception) {
            }
        }
    }

    /**
     * Save new Contract
     * */

    fun createNewContract(context: Context){
        try {
            Intent().apply {
                action = Intent.ACTION_INSERT
                data = ContactsContract.Contacts.CONTENT_URI
                context.startActivity(this)
            }
        }catch (e: Exception){
            context.showToast(context.getString(R.string.no_app_found))
        }

    }
}