package com.stork.blockspam.utils

import android.os.Build

fun isOreoMr1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

