package com.stork.blockspam.extension

import android.annotation.TargetApi
import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import android.telecom.TelecomManager

@TargetApi(Build.VERSION_CODES.M)
fun isMarshmallowPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

fun Context.isDefaultDialer(): Boolean {
    return if (!packageName.startsWith("com.simplemobiletools.contacts") && !packageName.startsWith("com.simplemobiletools.dialer")) {
        true
    } else if ((packageName.startsWith("com.simplemobiletools.contacts") || packageName.startsWith("com.simplemobiletools.dialer")) && isQPlus()) {
        val roleManager = getSystemService(RoleManager::class.java)
        roleManager!!.isRoleAvailable(RoleManager.ROLE_DIALER) && roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
    } else {
        isMarshmallowPlus() && telecomManager.defaultDialerPackage == packageName
    }
}

val Context.telecomManager: TelecomManager get() = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
