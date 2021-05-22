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

fun get2CharHeadOfName(name: String): String{
    val ls = name.trim().split(" ")
    if(ls.size == 1 && ls[0].length >= 2){
        return ls[0].substring(0, 2)
    }
    var value = ""
    ls.forEach { str: String ->
    }
    for ( index in ls.indices){
        if(ls[index].isNotEmpty()){
            value += (ls[index][0])
        }
        if(value.length == 2){break}
    }
    return value

}