package com.stork.viewcustom.loading

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.stork.viewcustom.R

class LoadingView(context: Context) :
        Dialog(context, R.style.LoadingView) {
    init {
        try {
            setContentView(R.layout.loading_view)
            val a: Animation = AnimationUtils.loadAnimation(context, R.anim.animation_loading)
            a.reset()
//            tv.clearAnimation()
//            tv.startAnimation(a)
            window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        }catch (e: Exception){
        }
    }
}