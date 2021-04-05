package com.stork.viewcustom.general;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;

import android.graphics.PorterDuff;

import android.graphics.drawable.GradientDrawable;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.stork.viewcustom.R;


public class TextViewAction extends AppCompatTextView {

    public TextViewAction(Context context) {
        super(context);
        init(context, null);
    }

    public TextViewAction(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TextViewAction(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            obtain(context, attrs);
        }
    }

    @SuppressLint("CustomViewStyleable")
    private void obtain(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RadiusLayout);
        if(!ta.getBoolean(R.styleable.RadiusLayout_offStateOnPressed, false)){
            setStateOnPressed();
        }

        ta.recycle();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setStateOnPressed(){
        setOnClickListener(new OnClickListener() {@Override public void onClick(View v) { }});
        setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    if(v.getBackground() == null){
                        GradientDrawable drawable = new GradientDrawable();
                        drawable.setCornerRadius(10f);
                        v.setBackground(drawable);
                    }
                    v.getBackground().setColorFilter(0x4DEFEFEF, PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if(v.getBackground() == null){
                        v.setBackground(new GradientDrawable());
                    }
                    v.getBackground().clearColorFilter();
                    v.invalidate();
                    break;
                }
            }
            return false;
        });
    }

    private float convertDp2Float(float dp) {

        return  Math.round((dp / getResources().getDisplayMetrics().density));
    }
}