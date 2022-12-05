package com.windmill.android.demo.custom;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.windmill.sdk.splash.IWMSplashEyeAd;
import com.windmill.sdk.splash.WMSplashEyeAdListener;

/**
 * created by lance on   2022/2/9 : 4:41 下午
 */

public class PangleSplashEyeAd implements IWMSplashEyeAd {

    private CSJSplashAd splashAD;
    private View splashView;
    private ViewGroup viewGroup;

    protected WMSplashEyeAdListener mSplashEyeAdListener;

    public PangleSplashEyeAd(CSJSplashAd splashAD, View splashView, ViewGroup viewGroup) {
        this.splashAD = splashAD;
        this.splashView = splashView;
        this.viewGroup = viewGroup;
    }

    public WMSplashEyeAdListener getSplashEyeAdListener() {
        return mSplashEyeAdListener;
    }

    @Override
    public void show(Context context, Rect rect, WMSplashEyeAdListener splashEyeAdListener) {
        try {
            mSplashEyeAdListener = splashEyeAdListener;
            if (splashEyeAdListener != null) {
                splashEyeAdListener.onAnimationStart(splashView);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public int[] getSuggestedSize(Context context) {
        int[] splashClickEyeSizeToDp;
        if (splashAD == null || context == null || (splashClickEyeSizeToDp = splashAD.getSplashClickEyeSizeToDp()) == null || splashClickEyeSizeToDp.length < 2) {
            return null;
        }
        return new int[]{dp2px(context, (float) splashClickEyeSizeToDp[0]), dp2px(context, (float) splashClickEyeSizeToDp[1])};
    }

    @Override
    public View getSplashView() {
        return splashView;
    }

    public int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public void onFinished() {
        if (splashAD != null) {
            splashAD.showSplashClickEyeView(viewGroup);
        }
    }

    @Override
    public void destroy() {
        try {
            this.mSplashEyeAdListener = null;

            if (this.splashView != null) {
                if (this.splashView.getParent() != null) {
                    ((ViewGroup) this.splashView.getParent()).removeView(this.splashView);
                }

                this.splashView = null;
            }

            this.splashAD = null;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
