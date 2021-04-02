package com.stork.blockspam

import android.app.Application
import com.stork.blockspam.database.AppControlDB
import com.stork.blockspam.storage.AppSharedPreferences

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        // init Room Database
        AppControlDB.getInstance(this)
        AppSharedPreferences.getInstance(this).saveInt(AppSharedPreferences.KEY_PREFERRENCE.TAB_SELECTED, 0)
    }
}