package com.stork.blockspam

import android.app.Application
import com.stork.blockspam.database.AppControlDB

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        // init Room Database
        AppControlDB.getInstance(this)
    }
}