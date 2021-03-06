package com.stork.blockspam

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telecom.TelecomManager
import androidx.annotation.RequiresApi
import com.stork.blockspam.base.BaseActivity
import com.stork.blockspam.utils.AppPermission
import java.lang.Exception


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setPermission()

    }

    private fun setPermission(){
        //set APP is caller id & spam app default
        requestRole()
        // Danh ba ---- READ_PHONE_STATE
        AppPermission.requirePermissions(this, AppPermission.PER_READ_PHONE_STATE, AppPermission.PER_REQUEST_CODE)
        AppPermission.requirePermissions(this, AppPermission.PER_READ_CONTACTS, AppPermission.PER_REQUEST_CODE)


        // Cach 2
        AppPermission.requirePermissions(this, AppPermission.PER_CALL_PHONE, AppPermission.PER_REQUEST_CODE)
        AppPermission.requirePermissions(this, AppPermission.PER_ANSWER_PHONE_CALLS, AppPermission.PER_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AppPermission.onRequestPermissionsResult(requestCode, permissions, grantResults) {state: Boolean ->

        }
    }

    /*
    *   set APP is caller id & spam app default
    *  dialog permission caller id & spam app
    *
    * */

    private val ROLE_CALL_SCREENING_ID = 100

    private fun requestRole() {
        try {
            val roleManager = getSystemService(Context.ROLE_SERVICE)
            if(roleManager  is RoleManager){
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                startActivityForResult(intent, ROLE_CALL_SCREENING_ID)
            }
        }catch (e: Exception){}
    }

    private fun setDefaultCallPhone(){
        val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
        intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
        startActivity(intent)
    }
    
    @RequiresApi(Build.VERSION_CODES.N)
    private fun gotoSettings(){
        var i = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        startActivity(i)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ROLE_CALL_SCREENING_ID) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                // Your app is now the call screening app
            } else {
                // Your app is not the call screening app
            }
        }
    }
}