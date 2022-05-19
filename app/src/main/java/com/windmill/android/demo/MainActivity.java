package com.windmill.android.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.windmill.android.demo.natives.NativeAdActivity;
import com.windmill.android.demo.splash.SplashEyeAdHolder;
import com.windmill.android.demo.splash.SplashZoomOutManager;
import com.windmill.sdk.WindMillAd;
import com.windmill.sdk.splash.WMSplashEyeAdListener;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        showSplashEyeAd();
    }

    private void showSplashEyeAd() {

        if (SplashEyeAdHolder.splashEyeAd == null) {
            return;
        }

        SplashEyeAdHolder.splashEyeAd.show(MainActivity.this, null, new WMSplashEyeAdListener() {
            @Override
            public void onAnimationStart(View splashView) {
                Log.i(TAG, "------------onAnimationStart---------");
                SplashZoomOutManager zoomOutManager = SplashZoomOutManager.getInstance(getApplicationContext());

                int[] suggestedSize = SplashEyeAdHolder.splashEyeAd.getSuggestedSize(getApplicationContext());
                if (suggestedSize != null) {
                    zoomOutManager.setSplashEyeAdViewSize(suggestedSize[0], suggestedSize[1]);
                }
                View zoomOutView = zoomOutManager.startZoomOutInTwoActivity((ViewGroup) getWindow().getDecorView(),
                        (ViewGroup) findViewById(android.R.id.content), new SplashZoomOutManager.AnimationCallBack() {

                            @Override
                            public void animationStart(int animationTime) {
                                Log.i(TAG, "------------animationStart---------");
                            }

                            @Override
                            public void animationEnd() {
                                Log.i(TAG, "------------animationEnd---------");
                                SplashEyeAdHolder.splashEyeAd.onFinished();
                            }
                        });

                if (zoomOutView != null) {
                    overridePendingTransition(0, 0);
                }
            }

            @Override
            public void onAdDismiss(boolean isSupportEyeSplash) {
                Log.i(TAG, "------------onAdDismiss---------" + isSupportEyeSplash);
                SplashZoomOutManager zoomOutManager = SplashZoomOutManager.getInstance(getApplicationContext());
                zoomOutManager.clearStaticData();

                SplashEyeAdHolder.splashEyeAd.destroy();
                SplashEyeAdHolder.splashEyeAd = null;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindMillAd.requestPermission(this);
        bindButton(R.id.bt_reward, RewardVideoActivity.class);
        bindButton(R.id.bt_interstitial, InterstitialActivity.class);
        bindButton(R.id.bt_splash, SplashAdActivity.class);
        bindButton(R.id.bt_native, NativeAdActivity.class);
        bindButton(R.id.bt_banner, BannerActivity.class);
        bindButton(R.id.bt_version, VersionActivity.class);
        bindButton(R.id.bt_device, DeviceActivity.class);
    }

    private void bindButton(@IdRes int id, final Class clz) {
        this.findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, clz);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //调用菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        try {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}