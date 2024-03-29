package com.stork.blockspam.extension

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import java.util.*

fun Int.getFormattedDuration(forceShowHours: Boolean = false): String {
    val sb = StringBuilder(8)
    val hours = this / 3600
    val minutes = this % 3600 / 60
    val seconds = this % 60

    if (this >= 3600) {
        sb.append(String.format(Locale.getDefault(), "%02d", hours)).append(":")
    } else if (forceShowHours) {
        sb.append("0:")
    }

    sb.append(String.format(Locale.getDefault(), "%02d", minutes))
    sb.append(":").append(String.format(Locale.getDefault(), "%02d", seconds))
    return sb.toString()
}


// if the given date is today, we show only the time. Else we show the date and optionally the time too
fun Int.formatDateOrTime(): String {
    val cal = Calendar.getInstance(Locale.ENGLISH)
    cal.timeInMillis = this * 1000L
    val TIME_FORMAT = "dd-MM-yyyy"
    return  DateFormat.format(TIME_FORMAT, cal).toString()
}

