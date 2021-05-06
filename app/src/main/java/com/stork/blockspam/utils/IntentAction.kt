package com.stork.blockspam.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity


object IntentAction {
    fun callPhone(context: Context, phone: String){
        val intentDial = Intent(Intent.ACTION_DIAL, Uri.parse("tel: ${phone}"))
        context.startActivity(intentDial)
        
    }

    fun sendSMS(context: Context, phone: String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null))
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        context.startActivity(intent)
    }

}