package com.stork.blockspam.service

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telecom.Call
import android.telecom.CallScreeningService
import androidx.room.Room
import com.stork.blockspam.database.AppDatabase
import com.stork.blockspam.database.CallPhone
import com.stork.blockspam.database.CallPhoneDAO


@TargetApi(Build.VERSION_CODES.O)
class BockSpamService : CallScreeningService() {

    override fun onScreenCall(details: Call.Details) {
        val responseBuilder = CallScreeningService.CallResponse.Builder()
        var response = responseBuilder.build()

        val tel = details.handle.schemeSpecificPart

        if (matchesContact(tel)) {
            respondToCall(details, response)
        }

        val database =
                Room.databaseBuilder(this, AppDatabase::class.java, AppDatabase.KEY_DATABASE)
                        .allowMainThreadQueries()
                        .build()

        val itemDAO: CallPhoneDAO = database.callPhoneDAO

        val items: List<CallPhone> = itemDAO.items


        // If a filter was tripped, reject the call.
        // TODO allow different behavior tied to filters
        // TODO OR results from multiple filters together to get final action

        response = responseBuilder
            .setDisallowCall(true)
            .setRejectCall(true)
            .setSkipCallLog(true)
            .setSkipNotification(true)
            .build()

        // In all unhandled cases, allow the call through.
        respondToCall(details, response)
    }

    private fun matchesContact(tel: String): Boolean {
        val query = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(tel))

        contentResolver.query(query,
            null,
            null,
            null,
            null
        ).use { results ->
            if (results?.count ?: 0 > 0) {
                return true
            }
        }

        return false
    }

}
