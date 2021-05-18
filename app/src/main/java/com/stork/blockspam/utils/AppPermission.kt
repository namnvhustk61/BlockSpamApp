package com.stork.blockspam.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.stork.blockspam.R
import com.stork.blockspam.extension.alert

object AppPermission {
    const val STATUS_SUCCESS :Int = 0    // đã dc cấp quyền
    const val STATUS_FAIL :Int    = 1    // chưa được cấp quyền
    const val STATUS_DENY :Int    = 2    // người dùng bật trạng thái đừng hỏi lại

    // BlockSpamService
    const val PER_READ_CONTACTS         =  Manifest.permission.READ_CONTACTS
    const val PER_READ_PHONE_STATE      =  Manifest.permission.READ_PHONE_STATE

    // BlockCallBroadcastReceiver
//    const val PER_READ_CONTACTS         =  Manifest.permission.READ_CONTACTS
    const val PER_READ_CALL_LOG    =  Manifest.permission.READ_CALL_LOG
    const val PER_CALL_PHONE    =  Manifest.permission.CALL_PHONE


    const val PER_ANSWER_PHONE_CALLS  =  Manifest.permission.ANSWER_PHONE_CALLS


    const val PER_REQUEST_CODE = 99
    const val PER_REQUEST_CODE_READ_PHONE = 999

    fun requirePermissions(activity: Activity, permissions: String, requestCode: Int):Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!hasPermission(activity, permissions)){
                activity.requestPermissions(arrayOf(permissions), requestCode)
                return false
            }
        }
        return true
    }

    fun requirePermissions(fragment: Fragment, permissions: String, requestCode: Int):Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!hasPermission(fragment.context!!, permissions)){
                fragment.requestPermissions(arrayOf(permissions), requestCode)
                return false
            }
        }
        return true
    }


    fun requirePermissions(activity: Activity, permissions: Array<String>, requestCode: Int):Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions.forEach { s ->
                if(!hasPermission(activity, s)){
                    activity.requestPermissions(permissions, requestCode)
                }
            }
        }
        return true
    }

     fun hasPermission(activity: Activity, permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.let {
                it.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
            }
        } else true
    }

    fun hasPermission(context: Context, permission: String ): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        callback: ((state: Boolean) -> Unit)?
    ){
        if (grantResults.isEmpty()){
            callback?.invoke(true)
            return
        }
        grantResults.forEach { grantResult: Int ->

            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                callback?.invoke(false)
                return
            }
            callback?.invoke(true)
        }

    }

    private fun showAlertReAskPermission(activity: Activity, permissions: String){
        val status = checkStatusPermission(
            activity,
            permissions
        )
       when(status){
           STATUS_FAIL, STATUS_DENY-> activity.alert(activity.getString(R.string.alert_re_permission))
       }
    }

    fun checkStatusPermission(activity: Activity, permission: String): Int{
        return if (hasPermission(activity, permission)){
            STATUS_SUCCESS
        }else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    permission
                )
            ){
                STATUS_FAIL
            }else{
                STATUS_DENY
            }
        }
    }
}
