package com.stork.viewcustom.extension

import android.content.Context
import android.os.Build
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Context.getColorResource(@ColorRes id: Int): Int {
    return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        getColor(id)
    } else {
        ContextCompat.getColor(this, id)
    }
}