package com.stork.blockspam.utils

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.telecom.TelecomManager
import androidx.annotation.RequiresApi
import java.lang.Exception

object AppSettingsManager {


    val ROLE_CALL_SCREENING_ID = 100
    fun setDefaultAppBlockCall(activity: Activity?):Boolean{
        try {
            val roleManager = activity?.getSystemService(Context.ROLE_SERVICE)
            if(roleManager  is RoleManager){
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                activity.startActivityForResult(intent, ROLE_CALL_SCREENING_ID)

                return true
            }
        }catch (e: Exception){
        }
        return false
    }

     fun setDefaultAppCallPhone(activity: Activity?){
        val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
        intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, activity?.packageName)
        activity?.startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun gotoSettingsDefaultApp(activity: Activity?){
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        activity?.startActivity(intent)
    }
}