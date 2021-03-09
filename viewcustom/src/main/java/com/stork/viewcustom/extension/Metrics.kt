package com.stork.viewcustom.extension

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue

fun Activity.displayMetrics(): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics
}

fun Activity.screenWidth(): Int {
    return displayMetrics().widthPixels
}

fun Activity.screenHeight(): Int {
    return displayMetrics().heightPixels
}

fun Context.dp(dp: Float): Int {
    return (dp * resources.displayMetrics.density).toInt();
}

fun Context.sp(sp: Float): Int {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            resources.displayMetrics
    ).toInt()
}

val Activity.screenWidth
    get() = screenWidth()

val Activity.screenHeight
    get() = screenHeight()