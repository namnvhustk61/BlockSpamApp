package com.stork.viewcustom.radius;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.stork.viewcustom.R;



public class RecycleViewRadius extends RecyclerView {

    private float[] radii = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};

    private int bgColor = Color.WHITE;

    private int strokeWidth = 0;
    private int strokeColor = Color.TRANSPARENT;


    public RecycleViewRadius(Context context) {
        super(context);
        init(context, null);
    }

    public RecycleViewRadius(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RecycleViewRadius(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            obtain(context, attrs);
        }

        setupBackground();
    }

    private void obtain(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RadiusLayout);
        bgColor = ta.getColor(R.styleable.RadiusLayout_backgroundColor, Color.WHITE);
        if (ta.hasValue(R.styleable.RadiusLayout_radius)) {
            float radius = ta.getDimensionPixelSize(R.styleable.RadiusLayout_radius, convertDp(0f));
            for(int i = 0; i< radii.length; i++){
                radii[i] = radius;
            }

        } else {
            float tl = ta.getDimensionPixelSize(R.styleable.RadiusLayout_radiusTopLeft, 0);
            float tr = ta.getDimensionPixelSize(R.styleable.RadiusLayout_radiusTopRight, 0);
            float br = ta.getDimensionPixelSize(R.styleable.RadiusLayout_radiusBottomRight, 0);
            float bl = ta.getDimensionPixelSize(R.styleable.RadiusLayout_radiusBottomLeft, 0);
            radii = new float[]{tl, tl, tr, tr, br, br, bl, bl};
        }
        strokeWidth = ta.getDimensionPixelSize(R.styleable.RadiusLayout_strokeWidth, 0);
        strokeColor = ta.getColor(R.styleable.RadiusLayout_strokeColor, Color.TRANSPARENT);

        ta.recycle();
    }

    private void setupBackground() {
        if (getBackground() instanceof GradientDrawable) {
            getBackground().mutate();
            ((GradientDrawable)getBackground()).setCornerRadii(radii);
            ((GradientDrawable)getBackground()).setStroke(strokeWidth, strokeColor);

        } else if (getBackground() instanceof ColorDrawable) {
            getBackground().mutate();
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(ColorStateList.valueOf(((ColorDrawable) getBackground()).getColor()));
            drawable.setCornerRadii(radii);
            drawable.setStroke(strokeWidth, strokeColor);
            setBackground(drawable);
        }

    }


    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        setupBackground();
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        setupBackground();
    }



    public void setStrokeWidth(int width) {
        this.strokeWidth = width;
        setupBackground();
    }

    private int convertDp(float dp) {

        return  Math.round((dp * getResources().getDisplayMetrics().density));
    }

}