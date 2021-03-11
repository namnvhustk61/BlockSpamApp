package com.stork.blockspam.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.stork.blockspam.ui.addcallphone.AddCallPhoneActivity

object AppNavigation {
    fun toAddCallPhone(context: Context) {
        val intent = Intent(context, AddCallPhoneActivity::class.java)
        context.startActivity(intent)
    }
}