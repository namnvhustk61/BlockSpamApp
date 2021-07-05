package com.stork.blockspam.extension

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.NotificationManager
import android.app.role.RoleManager
import android.content.Context
import android.database.Cursor
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.telecom.TelecomManager
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

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


fun Context.queryCursor(
        uri: Uri,
        projection: Array<String>,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null,
        showErrors: Boolean = false,
        callback: (cursor: Cursor) -> Unit
) {
    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    callback(cursor)
                } while (cursor.moveToNext())
            }
        }
    } catch (e: Exception) {
        if (showErrors) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT ).show()
        }
    }
}

@SuppressLint("MissingPermission")
fun Context.areMultipleSIMsAvailable(): Boolean {
    return try {
        telecomManager.callCapablePhoneAccounts.size > 1
    } catch (ignored: Exception) {
        false
    }
}

// CallingIncoming
val Context.audioManager: AudioManager get() = getSystemService(Context.AUDIO_SERVICE) as AudioManager

val Context.notificationManager: NotificationManager get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager