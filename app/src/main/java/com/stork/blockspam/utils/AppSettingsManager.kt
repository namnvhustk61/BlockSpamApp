package com.stork.blockspam.utils

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.telecom.TelecomManager
import androidx.annotation.RequiresApi
import com.stork.blockspam.extension.telecomManager
import java.lang.Exception

object AppSettingsManager {


    val ROLE_CALL_SCREENING_ID = 100
    val ROLE_CALL_DIAL_ID = 99
    fun setDefaultAppBlockCall(activity: Activity?):Boolean{
        try {
            val roleManager = activity?.getSystemService(Context.ROLE_SERVICE)
            if(roleManager  != null && roleManager is RoleManager){
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                activity.startActivityForResult(intent, ROLE_CALL_SCREENING_ID)

                return true
            }
        }catch (e: Exception){
        }
        return false
    }

     fun setDefaultAppCallPhone(activity: Activity?){
         try {
             val roleManager = activity?.getSystemService(Context.ROLE_SERVICE)
             if(roleManager  != null && roleManager is RoleManager){
                 val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                 activity.startActivityForResult(intent, ROLE_CALL_DIAL_ID)
                 return
             }
         }catch (e: Exception){
         }
         val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
         intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, activity?.packageName)
         activity?.startActivityForResult(intent, ROLE_CALL_DIAL_ID)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun gotoSettingsDefaultApp(activity: Activity?){
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        activity?.startActivity(intent)
    }


    fun setDefaultAppSMS(activity: Activity?):Boolean{
        try {
            val roleManager = activity?.getSystemService(Context.ROLE_SERVICE)
            if(roleManager  != null && roleManager is RoleManager){
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
                activity.startActivityForResult(intent, ROLE_CALL_SCREENING_ID)

                return true
            }
        }catch (e: Exception){
        }
        return false
    }

    fun isDefaultDialer(context: Context): Boolean {
        val default =  context.telecomManager.defaultDialerPackage
        return context.telecomManager.defaultDialerPackage == context.packageName
    }
}