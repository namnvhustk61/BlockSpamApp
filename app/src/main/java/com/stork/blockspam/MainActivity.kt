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
import com.stork.blockspam.utils.AppSettingsManager
import java.lang.Exception


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setPermission()

    }

    private fun setPermission(){
        //set APP is caller id & spam app default
        AppSettingsManager.setDefaultAppBlockCall(this)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsManager.ROLE_CALL_SCREENING_ID) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                // Your app is now the call screening app
            } else {
                // Your app is not the call screening app
            }
        }
    }
}