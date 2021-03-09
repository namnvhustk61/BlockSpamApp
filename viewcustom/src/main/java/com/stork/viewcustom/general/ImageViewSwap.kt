package com.stork.viewcustom.general

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.stork.viewcustom.R

class ImageViewSwap : AppCompatImageView {
    constructor(context: Context) : super(context) {init(context, null)}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {init(context, attrs)}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }
    private var activeIcon: Drawable? = null
    private var inactiveIcon: Drawable? = null


    private fun obtainValue(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TabBarView)
        activeIcon = ta.getDrawable(R.styleable.TabBarView_activeIcon)
        inactiveIcon = ta.getDrawable(R.styleable.TabBarView_inactiveIcon)
        ta.recycle()
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            obtainValue(context, attrs)
        }
        setActive(true)
    }


    fun setActive(isActive: Boolean) {
        setImageDrawable(if (isActive) {
            activeIcon
        } else {
            inactiveIcon
        })

    }
}