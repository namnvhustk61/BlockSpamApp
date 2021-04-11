package com.stork.viewcustom.general

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.core.view.setPadding
import com.stork.viewcustom.R
import com.stork.viewcustom.extension.dp
import com.stork.viewcustom.radius.LinearLayoutRadius


class TabBarView: LinearLayoutRadius {

    private var activeIcon: Drawable? = null
    private var inactiveIcon: Drawable? = null

    private var title = ""

    private lateinit var imageView : ImageView
    private lateinit var imgDot: ImageView

    private var isActive = false

    constructor(context: Context) : super(context) {init(context, null)}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {init(context, attrs)}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
            context,
            attrs,
            defStyleAttr,
            defStyleRes
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            obtainValue(context, attrs)
        }
        createIcon()
        createTitle()
        orientation = VERTICAL
        gravity = Gravity.CENTER
        setStateOnPressed()
    }

    private fun obtainValue(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TabBarView)
        activeIcon = ta.getDrawable(R.styleable.TabBarView_activeIcon)
        inactiveIcon = ta.getDrawable(R.styleable.TabBarView_inactiveIcon)
        title = ta.getString(R.styleable.TabBarView_title) ?: ""
        ta.recycle()
    }

    private fun createIcon() {
        setPadding(context.dp(8f))
        imageView = ImageView(context)
        val size = context.dp(24f)
        val params = LayoutParams(size, size)
//        params.bottomMargin = context.dp(5f)
        imageView.layoutParams = params
        imageView.setImageDrawable(if (isActive) {
            activeIcon
        } else {
            inactiveIcon
        })
        addView(imageView)
    }

    private fun createTitle() {
        imgDot = LayoutInflater.from(context).inflate(R.layout.tab_bar_view_dot, this, false) as ImageView
        addView(imgDot)
    }

    fun setActive(isActive: Boolean) {
        if (isActive) {
            imageView.setImageDrawable(activeIcon)
            imgDot.visibility = View.VISIBLE
        } else {
            imageView.setImageDrawable(inactiveIcon)
            imgDot.visibility = View.INVISIBLE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setStateOnPressed() {
        setOnTouchListener { v: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (v.background == null) {
                        val drawable = GradientDrawable()
                        height
                        drawable.cornerRadius = height.toFloat()
                        v.background = drawable
                    }
                    v.background.setColorFilter(context.getColor(R.color.color_press), PorterDuff.Mode.SRC_ATOP)

                    v.invalidate()
                }
                MotionEvent.ACTION_UP,  MotionEvent.ACTION_CANCEL -> {
                    if (v.background == null) {
                        v.background = GradientDrawable()
                    }
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
    }
}
