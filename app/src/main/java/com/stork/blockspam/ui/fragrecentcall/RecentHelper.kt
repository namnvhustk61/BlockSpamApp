package com.stork.blockspam.ui.fragrecentcall

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.provider.BlockedNumberContract
import android.provider.CallLog.Calls
import com.stork.blockspam.extension.*
import com.stork.blockspam.model.BlockedNumber
import com.stork.blockspam.model.PhoneContact
import com.stork.blockspam.ui.callingincome.CallManager.Companion.ensureBackgroundThread
import com.stork.blockspam.ui.callingincome.CallManager.Companion.getMyContactsCursor
import com.stork.blockspam.ui.callingincome.SimpleContactsHelper
import com.stork.blockspam.utils.AppPermission
import com.stork.blockspam.utils.getAvailableSIMCardLabels


class RecentHelper(private val context: Context) {
    private val COMPARABLE_PHONE_NUMBER_LENGTH = 9

    @SuppressLint("MissingPermission")
    fun getRecentCalls(groupSubsequentCalls: Boolean, callback: (ArrayList<RecentCall>) -> Unit) {

        ensureBackgroundThread {
            if (!AppPermission.hasPermission(context, AppPermission.PER_READ_CALL_LOG)) {
                callback(ArrayList())
                return@ensureBackgroundThread
            }

            SimpleContactsHelper(context).getAvailableContacts(false) { contacts ->

                getRecents(contacts, groupSubsequentCalls, callback)
            }
        }
    }

    private fun getRecents(contacts: ArrayList<PhoneContact>, groupSubsequentCalls: Boolean, callback: (ArrayList<RecentCall>) -> Unit) {
        var recentCalls = ArrayList<RecentCall>()
        var previousRecentCallFrom = ""
        val contactsNumbersMap = HashMap<String, String>()
        val uri = Calls.CONTENT_URI
        val projection = arrayOf(
                Calls._ID,
                Calls.NUMBER,
                Calls.CACHED_NAME,
                Calls.CACHED_PHOTO_URI,
                Calls.DATE,
                Calls.DURATION,
                Calls.TYPE,
                "phone_account_address"
        )

        val numberToSimIDMap = HashMap<String, Int>()
        context.getAvailableSIMCardLabels().forEach {
            numberToSimIDMap[it.phoneNumber] = it.id
        }

        val sortOrder = "${Calls._ID} DESC LIMIT 100"
        context.queryCursor(uri, projection, sortOrder = sortOrder, showErrors = true) { cursor ->
            val id = cursor.getIntValue(Calls._ID)
            val number = cursor.getStringValue(Calls.NUMBER)
            var name = cursor.getStringValue(Calls.CACHED_NAME)
            if (name == null || name.isEmpty()) {
                name = number
            }

            if (name == number) {
                if (contactsNumbersMap.containsKey(number)) {
                    name = contactsNumbersMap[number]!!
                } else {
                    val normalizedNumber = number.normalizePhoneNumber()
                    if (normalizedNumber!!.length >= COMPARABLE_PHONE_NUMBER_LENGTH) {
                        name = contacts.firstOrNull { contact ->
                            val curNumber = contact.phoneNumbers.first().normalizePhoneNumber()
                            if (curNumber!!.length >= COMPARABLE_PHONE_NUMBER_LENGTH) {
                                if (curNumber.substring(curNumber.length - COMPARABLE_PHONE_NUMBER_LENGTH) == normalizedNumber.substring(normalizedNumber.length - COMPARABLE_PHONE_NUMBER_LENGTH)) {
                                    contactsNumbersMap[number] = contact.name
                                    return@firstOrNull true
                                }
                            }
                            false
                        }?.name ?: number
                    }
                }
            }

            val photoUri = cursor.getStringValue(Calls.CACHED_PHOTO_URI) ?: ""
            val startTS = (cursor.getLongValue(Calls.DATE) / 1000L).toInt()
            val duration = cursor.getIntValue(Calls.DURATION)
            val type = cursor.getIntValue(Calls.TYPE)
            val accountAddress = cursor.getStringValue("phone_account_address")
            val simID = numberToSimIDMap[accountAddress] ?: 1
            val neighbourIDs = ArrayList<Int>()
            val recentCall = RecentCall(id, number, name, photoUri, startTS, duration, type, neighbourIDs, simID)

            // if we have multiple missed calls from the same number, show it just once
            if (!groupSubsequentCalls || "$number$name" != previousRecentCallFrom) {
                recentCalls.add(recentCall)
            } else {
                recentCalls.lastOrNull()?.neighbourIDs?.add(id)
            }

            previousRecentCallFrom = "$number$name"
        }

        val blockedNumbers = context.getBlockedNumbers()
        recentCalls = recentCalls.filter { !context.isNumberBlocked(it.phoneNumber, blockedNumbers) }.toMutableList() as ArrayList<RecentCall>
        callback(recentCalls)
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun Context.getBlockedNumbers(): java.util.ArrayList<BlockedNumber> {
        val blockedNumbers = java.util.ArrayList<BlockedNumber>()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || !isDefaultDialer()) {
            return blockedNumbers
        }

        val uri = BlockedNumberContract.BlockedNumbers.CONTENT_URI
        val projection = arrayOf(
                BlockedNumberContract.BlockedNumbers.COLUMN_ID,
                BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER
        )

        queryCursor(uri, projection) { cursor ->
            val id = cursor.getLongValue(BlockedNumberContract.BlockedNumbers.COLUMN_ID)
            val number = cursor.getStringValue(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER) ?: ""
            val normalizedNumber = cursor.getStringValue(BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER) ?: number
            val comparableNumber = normalizedNumber.trimToComparableNumber()
            val blockedNumber = BlockedNumber(id, number, normalizedNumber, comparableNumber)
            blockedNumbers.add(blockedNumber)
        }

        return blockedNumbers
    }

    fun Context.isNumberBlocked(number: String, blockedNumbers: java.util.ArrayList<BlockedNumber> = getBlockedNumbers()): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return false
        }

        val numberToCompare = number.trimToComparableNumber()
        return blockedNumbers.map { it.numberToCompare }.contains(numberToCompare) || blockedNumbers.map { it.number }.contains(numberToCompare)
    }


}