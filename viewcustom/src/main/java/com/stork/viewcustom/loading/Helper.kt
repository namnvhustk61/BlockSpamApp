package com.stork.viewcustom.loading

import android.graphics.Color
import java.util.*

object Helper {

    fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = Math.round(Color.alpha(color) * factor)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }
}

fun ClosedFloatingPointRange<Float>.random() =
        (Random().nextFloat() * (endInclusive - start)) + start

fun ClosedRange<Int>.random() =
        Random().nextInt((endInclusive + 1) - start) + start

