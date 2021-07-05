package com.stork.viewcustom.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.stork.viewcustom.R;
import com.stork.viewcustom.radius.LinearLayoutRadius;

import org.w3c.dom.Text;

import java.util.List;

import kotlin.Unit;

public class PopupWindowMenu extends PopupWindow {

    public interface Itf{
        void onClickItemMenu(int index);
    }
    List<String> lsTitle;
    List<Drawable> lsResIdDrawable;
    @SuppressLint("UseCompatLoadingForDrawables")
    public PopupWindowMenu(Context context, List<String> lsTitle, List<Drawable> lsResIdDrawable, @ColorInt int colorBg, Itf itf){
        this.lsTitle = lsTitle;
        this.lsResIdDrawable = lsResIdDrawable;
        LinearLayoutRadius vRoot = new LinearLayoutRadius(context);
        vRoot.setOrientation(LinearLayout.VERTICAL);
        int dp = convertDp(context, 10f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);

        for(int i=0; i<lsTitle.size(); i++){
            layoutParams = new LinearLayout.LayoutParams(-2, -2);
            if(i == 0){
                layoutParams.setMargins(dp, dp, dp, dp);
            }else {
                layoutParams.setMargins(dp, 0, dp, dp);
            }

            LinearLayoutRadius item = viewItem(context, lsResIdDrawable.get(i),  lsTitle.get(i));
            int index = i;
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itf.onClickItemMenu(index);
                    PopupWindowMenu.this.dismiss();
                }
            });
            vRoot.addView(
                    item
                    , layoutParams
            );
        }

        GradientDrawable bg = new GradientDrawable();
        bg.setColorFilter(colorBg, PorterDuff.Mode.SRC_ATOP);
        vRoot.setBackground(bg);
        vRoot.setRadii(dp);
        vRoot.setElevation(dp);

        LinearLayout vElement = new LinearLayout(context);
        bg = new GradientDrawable();
        int colorBgStroke = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            colorBgStroke = context.getColor(R.color.stroke);
        } else {
            colorBgStroke =  ContextCompat.getColor(context, R.color.stroke);
        }
        bg.setColorFilter(colorBgStroke, PorterDuff.Mode.SRC_ATOP);

        bg.setAlpha(60);
        bg.setCornerRadius(dp);
        vElement.setBackground(bg);
        layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.setMargins(3, 3, 3, 3);
        vElement.addView(vRoot, layoutParams);

        LinearLayoutRadius vParent = new LinearLayoutRadius(context);
        layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.setMargins(0, 0, dp*3/2, 0);
        vParent.addView(vElement, layoutParams);
        setContentView(vParent);
        //
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
    }

    private int convertDp(Context context, float dp) {

        return  Math.round((dp * context.getResources().getDisplayMetrics().density));
    }


    private LinearLayoutRadius viewItem(Context context, Drawable icon, String title){
        LinearLayoutRadius vRoot = new LinearLayoutRadius(context);
        vRoot.setStateOnPressed();
        vRoot.setOrientation(LinearLayout.HORIZONTAL);
        ImageView img = new ImageView(context);
        img.setLayoutParams(new LinearLayout.LayoutParams(convertDp(context, 20), convertDp(context, 20)));
        img.setImageDrawable(icon);
        TextView tv = new TextView(context);
        tv.setTextSize(16f);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.svn_gilroy_medium);
        tv.setTypeface(typeface);
        tv.setText(title);

        int colorBgStroke = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            colorBgStroke = context.getColor(R.color.text);
        } else {
            colorBgStroke =  ContextCompat.getColor(context, R.color.text);
        }
        tv.setTextColor(colorBgStroke);


        vRoot.addView(img);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(convertDp(context, 10), 0, convertDp(context, 10),0);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        vRoot.addView(tv, layoutParams);

        return vRoot;
    }

}
