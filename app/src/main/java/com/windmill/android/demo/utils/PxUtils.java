package com.windmill.android.demo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class PxUtils {

    public static int dpToPx(Context context, int dp) {
        Resources r = context.getApplicationContext().getResources();
        float px = TypedValue.applyDimension(1, (float) dp, r.getDisplayMetrics());
        return (int) px;
    }

    public static int pxToDp(Context context, int px) {
        float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) ((float) px / scale + 0.5F);
    }

    public static int getDeviceWidthInPixel(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getDeviceHeightInPixel(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }
}
