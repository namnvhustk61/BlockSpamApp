package com.stork.blockspam.ui.callingincome


import android.content.Context
import android.net.Uri

import android.os.Looper
import android.telecom.Call
import android.telecom.InCallService
import android.telecom.VideoProfile
import androidx.loader.content.CursorLoader
import com.stork.blockspam.model.PhoneContact

class CallManager {

    companion object {
        var call: Call? = null
        var inCallService: InCallService? = null

        fun accept() {
            call?.answer(VideoProfile.STATE_AUDIO_ONLY)
        }

        fun reject() {
            if (call != null) {
                if (call!!.state == Call.STATE_RINGING) {
                    call!!.reject(false, null)
                } else {
                    call!!.disconnect()
                }
            }
        }

        fun registerCallback(callback: Call.Callback) {
            if (call != null) {
                call!!.registerCallback(callback)
            }
        }

        fun unregisterCallback(callback: Call.Callback) {
            call?.unregisterCallback(callback)
        }

        fun getState() = if (call == null) {
            Call.STATE_DISCONNECTED
        } else {
            call!!.state
        }

        fun keypad(c: Char) {
            call?.playDtmfTone(c)
            call?.stopDtmfTone()
        }



        fun getCallContact(context: Context, callback: (PhoneContact?) -> Unit) {
            ensureBackgroundThread {
                val callContact = PhoneContact(-1, -1, "", "", arrayListOf(), arrayListOf(), arrayListOf())
                if (call == null || call!!.details == null || call!!.details!!.handle == null) {
                    callback(callContact)
                    return@ensureBackgroundThread
                }

                val uri = Uri.decode(call!!.details.handle.toString())
                if (uri.startsWith("tel:")) {
                    val number = uri.substringAfter("tel:")
                    callContact.phoneNumbers = arrayListOf(number)
                    callContact.name = SimpleContactsHelper(context).getNameFromPhoneNumber(number)
                    callContact.photoUri = SimpleContactsHelper(context).getPhotoUriFromPhoneNumber(number)

                    callback(callContact)

                }
            }
        }

        private  val AUTHORITY = "com.simplemobiletools.commons.contactsprovider"
        val CONTACTS_CONTENT_URI = Uri.parse("content://$AUTHORITY/contacts")
        fun Context.getMyContactsCursor(favoritesOnly: Boolean, withPhoneNumbersOnly: Boolean) = try {
            val getFavoritesOnly = if (favoritesOnly) "1" else "0"
            val getWithPhoneNumbersOnly = if (withPhoneNumbersOnly) "1" else "0"
            val args = arrayOf(getFavoritesOnly, getWithPhoneNumbersOnly)
            CursorLoader(this, CONTACTS_CONTENT_URI, null, null, args, null)
        } catch (e: Exception) {
            null
        }
        private fun ensureBackgroundThread(callback: () -> Unit) {
            if (isOnMainThread()) {
                Thread {
                    callback()
                }.start()
            } else {
                callback()
            }
        }
        private fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()
    }






}