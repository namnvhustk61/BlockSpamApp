package com.stork.blockspam.extension

import com.stork.blockspam.R
import java.util.*

private val lsBgDrawable = listOf(
        R.drawable.ic_text_view_round_1,
        R.drawable.ic_text_view_round_2,
        R.drawable.ic_text_view_round_3,
        R.drawable.ic_text_view_round_4,
        R.drawable.ic_text_view_round_5
)
var random: Random = Random()
fun getRandomBgDrawable(): Int{
    return lsBgDrawable[random.nextInt(lsBgDrawable.size)]
}