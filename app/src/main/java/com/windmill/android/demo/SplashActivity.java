package com.windmill.android.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.windmill.android.demo.splash.SplashEyeAdHolder;
import com.windmill.android.demo.splash.SplashZoomOutManager;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.models.AdInfo;
import com.windmill.sdk.splash.IWMSplashEyeAd;
import com.windmill.sdk.splash.WMSplashAd;
import com.windmill.sdk.splash.WMSplashAdListener;
import com.windmill.sdk.splash.WMSplashAdRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends Activity {
    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    public boolean canJumpImmediately = false;
    private boolean isFullScreen = false;
    private boolean isSelfLogo = false;
    private WMSplashAd splashAd;
    private ViewGroup adContainer;
    private String placementId;
    private String userId = "123456789";

    // 是否适配全面屏，默认是适配全面屏，即使用顶部状态栏和底部导航栏
    private boolean isNotchAdaptation = true;

    private void getExtraInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("setting", 0);
        isSelfLogo = sharedPreferences.getBoolean(Constants.CONF_SELF_LOGO, false);
        isFullScreen = sharedPreferences.getBoolean(Constants.CONF_FULL_SCREEN, false);
        placementId = sharedPreferences.getString(Constants.CONF_PLACEMENT_ID, "");

        if (TextUtils.isEmpty(placementId)) {
            String[] stringArray = getResources().getStringArray(R.array.splash_id_value);
            placementId = stringArray[0];
        }
    }

    private void hideSystemUI() {
        int systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        } else {
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        Window window = this.getWindow();
        window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        // 五要素隐私详情页或五要素弹窗关闭回到开屏广告时，再次设置SystemUi
        window.getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> setSystemUi());

        // Android P 官方方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(params);
        }
    }

    private void showSystemUI() {
        int systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        Window window = this.getWindow();
        window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        // 五要素隐私详情页或五要素弹窗关闭回到开屏广告时，再次设置SystemUi
        window.getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> setSystemUi());
    }

    private void setSystemUi() {
        if (!isNotchAdaptation) {
            showSystemUI();
        } else {
            hideSystemUI();
        }
    }

    public int dipsToIntPixels(final float dips, final Context context) {
        return (int) (dips * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    private DisplayMetrics getRealMetrics(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            @SuppressWarnings("rawtypes")
            Class c;
            try {
                c = Class.forName("android.view.Display");
                @SuppressWarnings("unchecked")
                Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, dm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 如需适配刘海屏水滴屏，必须在onCreate方法中设置全屏显示
        if (isNotchAdaptation) {
            hideSystemUI();
        }

        setContentView(R.layout.activity_splash);
        adContainer = findViewById(R.id.splash_container);

        getExtraInfo();

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", userId);
        options.put(WMConstants.AD_WIDTH, getRealMetrics(this).widthPixels);//针对于穿山甲、GroMore开屏有效、单位px
        options.put(WMConstants.AD_HEIGHT, getRealMetrics(this).heightPixels - dipsToIntPixels(100, this));//针对于穿山甲、GroMore开屏有效、单位px
        WMSplashAdRequest splashAdRequest = new WMSplashAdRequest(placementId, userId, options);

        /**
         *  广告结束，广告内容是否自动隐藏。
         *  若开屏和应用共用Activity，建议false。
         *  开屏是单独Activity ，建议true。
         */
        splashAdRequest.setDisableAutoHideAd(true);

        /**
         * 设置应用LOGO标题及描述,半屏Window展示
         */
        if (isSelfLogo) {
            splashAdRequest.setAppTitle("开心消消乐");
            splashAdRequest.setAppDesc("让世界充满快乐");
        }

        splashAd = new WMSplashAd(this, splashAdRequest, new WMSplashAdListener() {
            @Override
            public void onSplashAdSuccessPresent(AdInfo adInfo) {
                Log.d("lance", "------onSplashAdSuccessPresent------" + adInfo.getPlacementId());
            }

            @Override
            public void onSplashAdSuccessLoad(String placementId) {
                Log.d("lance", "------onSplashAdSuccessLoad------" + splashAd.isReady() + ":" + placementId);
            }

            @Override
            public void onSplashAdFailToLoad(WindMillError windMillError, String placementId) {
                Log.d("lance", "------onSplashAdFailToLoad------" + windMillError.toString() + ":" + placementId);
                jumpMainActivity();
            }

            @Override
            public void onSplashAdClicked(AdInfo adInfo) {
                Log.d("lance", "------onSplashAdClicked------" + adInfo.getPlacementId());
            }

            @Override
            public void onSplashClosed(AdInfo adInfo, IWMSplashEyeAd splashEyeAd) {
                Log.d("lance", "------onSplashClosed------" + adInfo.getPlacementId());
                SplashEyeAdHolder.splashEyeAd = splashEyeAd;
                jumpWhenCanClick();
            }
        });

        if (isFullScreen) {//全屏开屏Window展示
            splashAd.loadAdAndShow(null);
        } else {//采用容器展示开屏广告内容
            splashAd.loadAdAndShow(adContainer);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJumpImmediately = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJumpImmediately) {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
    }

    private void jumpWhenCanClick() {
        if (canJumpImmediately) {
            jumpMainActivity();
        } else {
            canJumpImmediately = true;
        }
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jumpMainActivity() {

        if (SplashEyeAdHolder.splashEyeAd != null) {
            try {
                SplashZoomOutManager zoomOutManager = SplashZoomOutManager.getInstance(getApplicationContext());
                zoomOutManager.setSplashInfo(SplashEyeAdHolder.splashEyeAd.getSplashView(), getWindow().getDecorView());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        overridePendingTransition(0, 0);

        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
