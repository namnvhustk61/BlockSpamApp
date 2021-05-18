package com.stork.blockspam.ui.callingincome


import android.content.Context
import android.net.Uri
import android.provider.ContactsContract.*
import com.stork.blockspam.extension.getStringValue
import com.stork.blockspam.utils.AppPermission


class SimpleContactsHelper(val context: Context) {

    fun getNameFromPhoneNumber(number: String): String {
        if (!AppPermission.hasPermission(context, AppPermission.PER_READ_CONTACTS)) {
            return number
        }

        val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val projection = arrayOf(
                PhoneLookup.DISPLAY_NAME
        )

        try {
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor.use {
                if (cursor?.moveToFirst() == true) {
                    return cursor.getStringValue(PhoneLookup.DISPLAY_NAME)
                }
            }
        } catch (e: Exception) {
        }
        return number
    }

    fun getPhotoUriFromPhoneNumber(number: String): String {
        if (!AppPermission.hasPermission(context, AppPermission.PER_READ_CONTACTS)) {
            return ""
        }

        val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val projection = arrayOf(
                PhoneLookup.PHOTO_URI
        )

        try {
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor.use {
                if (cursor?.moveToFirst() == true) {
                    return cursor.getStringValue(PhoneLookup.PHOTO_URI) ?: ""
                }
            }
        } catch (e: Exception) {
        }
        return ""
    }

}
