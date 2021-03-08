package com.stork.blockspam

import android.content.Intent
import android.os.Bundle
import androidx.room.Room
import com.stork.blockspam.base.BaseActivity
import com.stork.blockspam.database.AppDatabase
import com.stork.blockspam.database.CallPhone
import com.stork.blockspam.database.CallPhoneDAO
import com.stork.blockspam.utils.AppPermission
import com.stork.blockspam.utils.AppSettingsManager


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, AppDatabase.KEY_DATABASE)
                .allowMainThreadQueries()
                .build()

        val itemDAO: CallPhoneDAO = database.callPhoneDAO

        val items: List<CallPhone> = itemDAO.items


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