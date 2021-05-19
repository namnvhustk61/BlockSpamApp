package com.stork.blockspam.ui.fragrecentcall

import android.telephony.PhoneNumberUtils

data class RecentCall(var id: Int, var phoneNumber: String, var name: String, var photoUri: String, var startTS: Int, var duration: Int, var type: Int,
                      var neighbourIDs: ArrayList<Int>, val simID: Int) {
    fun doesContainPhoneNumber(text: String): Boolean {
        val normalizedText = text.normalizePhoneNumber()
        return PhoneNumberUtils.compare(phoneNumber.normalizePhoneNumber(), normalizedText) ||
                phoneNumber.contains(text) ||
                phoneNumber.normalizePhoneNumber().contains(normalizedText) ||
                phoneNumber.contains(normalizedText)
    }

    fun String.normalizePhoneNumber() = PhoneNumberUtils.normalizeNumber(this)
}