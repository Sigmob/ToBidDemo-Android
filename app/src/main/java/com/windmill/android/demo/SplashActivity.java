package com.windmill.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.windmill.android.demo.splash.SplashEyeAdHolder;
import com.windmill.android.demo.splash.SplashZoomOutManager;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.models.AdInfo;
import com.windmill.sdk.splash.IWMSplashEyeAd;
import com.windmill.sdk.splash.WMSplashAd;
import com.windmill.sdk.splash.WMSplashAdListener;
import com.windmill.sdk.splash.WMSplashAdRequest;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        adContainer = findViewById(R.id.splash_container);

        getExtraInfo();

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", userId);
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
