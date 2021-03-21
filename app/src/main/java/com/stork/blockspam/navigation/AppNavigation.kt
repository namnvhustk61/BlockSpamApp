package com.stork.blockspam.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.stork.blockspam.R
import com.stork.blockspam.ui.addcallphone.AddCallPhoneActivity

object AppNavigation {
    private fun baseStart(context: Context, intent: Intent){
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(R.anim.enter_zoom_in, R.anim.exit_zoom_out);
    }

    fun toAddCallPhone(context: Context) {
        val intent = Intent(context, AddCallPhoneActivity::class.java)
       baseStart(context, intent)
    }

}