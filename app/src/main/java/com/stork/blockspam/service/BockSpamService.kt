package com.stork.blockspam.service

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telecom.Call
import android.telecom.CallScreeningService
import com.stork.blockspam.database.model.CallPhone.CallPhone
import com.stork.blockspam.utils.AppPermission


@TargetApi(Build.VERSION_CODES.O)
class BockSpamService : CallScreeningService() {

    override fun onScreenCall(details: Call.Details) {
        if (!AppPermission.hasPermission(this, AppPermission.PER_READ_PHONE_STATE)
                || !AppPermission.hasPermission(this, AppPermission.PER_READ_CONTACTS)) {
            return;
        }
        val responseBuilder = CallScreeningService.CallResponse.Builder()
        var response = responseBuilder.build()

        val tel = details.handle.schemeSpecificPart

        if (matchesContact(tel)) {
            respondToCall(details, response)
        }
        val isBlock: Boolean =  CallPhone.hasDB(this, tel)

        // If a filter was tripped, reject the call.
        // TODO allow different behavior tied to filters
        // TODO OR results from multiple filters together to get final action

        // isBlock = true --> block
        response = responseBuilder
            .setDisallowCall(isBlock)
            .setRejectCall(isBlock)
            .setSkipCallLog(isBlock)
            .setSkipNotification(isBlock)
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
