package com.stork.blockspam.extension

import android.telephony.PhoneNumberUtils
import java.text.Normalizer

fun String.normalizePhoneNumber() = PhoneNumberUtils.normalizeNumber(this)

// if we are comparing phone numbers, compare just the last 9 digits
fun String.trimToComparableNumber(): String {
    val normalizedNumber = this.normalizeString()
    val startIndex = Math.max(0, normalizedNumber.length - 9)
    return normalizedNumber.substring(startIndex)
}

// remove diacritics, for example č -> c
val normalizeRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
fun String.normalizeString() = Normalizer.normalize(this, Normalizer.Form.NFD).replace(normalizeRegex, "")