package com.stork.viewcustom.radius;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.stork.viewcustom.R;


public class TextViewGradient extends AppCompatTextView {

    int gradientColorStart;
    int gradientColorEnd;

    public TextViewGradient(Context context) {
        super(context);
        init(context, null);

    }

    public TextViewGradient(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public TextViewGradient(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }



    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            obtain(context, attrs);
        }

        setPain();
    }

    @SuppressLint("Recycle")
    private void obtain(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TextViewGradient);

        if (ta.hasValue(R.styleable.TextViewGradient_colorStart)) {
            gradientColorStart = ta.getColor(R.styleable.TextViewGradient_colorStart, Color.BLUE);

        } else {
            gradientColorStart = getResources().getColor(R.color.gradient_start);
        }

        if (ta.hasValue(R.styleable.TextViewGradient_colorEnd)) {
            gradientColorEnd = ta.getColor(R.styleable.TextViewGradient_colorEnd, Color.CYAN);

        } else {
            gradientColorEnd =  getResources().getColor(R.color.gradient_end);
        }
    }



    private void setPain() {
        Paint paint = getPaint();

        float width = paint.measureText(getText().toString());
        Shader textShader = new LinearGradient(0f, 0f, width, getTextSize(),
                new int[]{
                        gradientColorStart,
                        gradientColorEnd


                },
                null, Shader.TileMode.REPEAT
        );

        getPaint().setShader(textShader);
    }


}
