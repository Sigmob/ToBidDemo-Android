package com.windmill.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.windmill.sdk.WindMillError;
import com.windmill.sdk.splash.WMSplashAd;
import com.windmill.sdk.splash.WMSplashAdListener;
import com.windmill.sdk.splash.WMSplashAdRequest;

import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends Activity implements WMSplashAdListener {

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    public boolean canJumpImmediately = false;
    private WMSplashAd splashAd;
    private ViewGroup adContainer;

    private boolean isLoadAndShow = true;//是否实时加载并显示开屏广告
    private boolean isFullScreen = false;
    private boolean isSelfLogo = false;
    private boolean needStartMainActivity = true;

    private String placementId;
    private String userId = "123456789";

    private void getExtraInfo() {
        Intent intent = getIntent();
        isLoadAndShow = intent.getBooleanExtra("isLoadAndShow", true);
        isFullScreen = intent.getBooleanExtra("isFullScreen", false);
        isSelfLogo = intent.getBooleanExtra("isLoadAndShow", false);
        placementId = intent.getStringExtra("placementId");
        needStartMainActivity = intent.getBooleanExtra("need_start_main_activity", true);
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

        splashAd = new WMSplashAd(this, splashAdRequest, this);

        if (isLoadAndShow) {
            if (isFullScreen) {//全屏开屏Window展示
                splashAd.loadAdAndShow(null);
            } else {//采用容器展示开屏广告内容
                splashAd.loadAdAndShow(adContainer);
            }
        } else {
            splashAd.loadAdOnly();
        }
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
        if (needStartMainActivity) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            this.startActivity(intent);
        }
        this.finish();
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

    @Override
    public void onSplashAdSuccessPresent() {
        Log.d("lance", "------onSplashAdSuccessPresent------");
    }

    @Override
    public void onSplashAdSuccessLoad() {
        Log.d("lance", "------onSplashAdSuccessLoad------" + splashAd.isReady());
        if (!isLoadAndShow && splashAd.isReady()) {
            if (isFullScreen) {//全屏开屏Window展示
                splashAd.showAd(null);
            } else {//采用容器展示开屏广告内容
                splashAd.showAd(adContainer);
            }
        }
    }

    @Override
    public void onSplashAdFailToLoad(WindMillError windMillError, String placementId) {
        Log.d("lance", "------onSplashAdFailToLoad------" + windMillError.toString() + ":" + placementId);
        jumpMainActivity();
    }

    @Override
    public void onSplashAdClicked() {
        Log.d("lance", "------onSplashAdClicked------");
    }

    @Override
    public void onSplashClosed() {
        Log.d("lance", "------onSplashClosed------");
        jumpWhenCanClick();
    }
}
