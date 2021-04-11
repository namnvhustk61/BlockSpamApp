package com.stork.viewcustom.radius;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.stork.viewcustom.R;


public class ImageViewRadius extends AppCompatImageView {

    public ImageViewRadius(Context context) {
        super(context);
        init(context, null);
    }

    public ImageViewRadius(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImageViewRadius(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    private float[] radii ;
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            obtain(context, attrs);
        }

    }

    @SuppressLint("CustomViewStyleable")
    private void obtain(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RadiusLayout);
        if (ta.hasValue(R.styleable.RadiusLayout_radius)) {
            float radius = ta.getDimensionPixelSize(R.styleable.RadiusLayout_radius, 0);
            radii = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
            for(int i = 0; i< radii.length; i++){
                radii[i] = convertDp2Float(radius);
            }

        } else {
            float tl = ta.getDimensionPixelSize(R.styleable.RadiusLayout_radiusTopLeft, 0);
            float tr = ta.getDimensionPixelSize(R.styleable.RadiusLayout_radiusTopRight, 0);
            float br = ta.getDimensionPixelSize(R.styleable.RadiusLayout_radiusBottomRight, 0);
            float bl = ta.getDimensionPixelSize(R.styleable.RadiusLayout_radiusBottomLeft, 0);
            radii = new float[]{tl, tl, tr, tr, br, br, bl, bl};
        }

        if(!ta.getBoolean(R.styleable.RadiusLayout_offStateOnPressed, false)){
            setStateOnPressed();
        }

        ta.recycle();
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        // Round some corners betch!
        Drawable myDrawable = getDrawable();

        if (myDrawable instanceof BitmapDrawable && radii != null) {
            Paint paint = ((BitmapDrawable) myDrawable).getPaint();
            final int color = 0xff000000;
            Rect bitmapBounds = myDrawable.getBounds();
            final RectF rectF = new RectF(bitmapBounds);
            // Create an off-screen bitmap to the PorterDuff alpha blending to work right
            int saveCount = canvas.saveLayer(rectF, null,
                    Canvas.ALL_SAVE_FLAG);
            // Resize the rounded rect we'll clip by this view's current bounds
            // (super.onDraw() will do something similar with the drawable to draw)
            getImageMatrix().mapRect(rectF);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
//            canvas.drawRoundRect(rectF, radii[0], radii[0], paint);
            final Path path = new Path();
            path.addRoundRect(rectF, radii, Path.Direction.CW);
            canvas.drawPath(path, paint);

            Xfermode oldMode = paint.getXfermode();
            // This is the paint already associated with the BitmapDrawable that super draws
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//            super.onDraw(canvas);
            paint.setXfermode(oldMode);
            canvas.restoreToCount(saveCount);
        }else if(myDrawable instanceof LayerDrawable && radii != null){
            if(((LayerDrawable) myDrawable).getDrawable(0) instanceof GradientDrawable){
                GradientDrawable drawBg = (GradientDrawable)((LayerDrawable) myDrawable).getDrawable(0);
                drawBg.setCornerRadii(radii);
                setImageDrawable(myDrawable);
            }

        }
        super.onDraw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setStateOnPressed(){
        setOnClickListener(new OnClickListener() {@Override public void onClick(View v) { }});
        setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    if(v.getBackground() == null){
                        GradientDrawable drawable = new GradientDrawable();
                        drawable.setCornerRadius(15f);
                        v.setBackground(drawable);
                    }
                    v.getBackground().setColorFilter(getContext().getColor(R.color.color_press), PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP: case MotionEvent.ACTION_CANCEL: {
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