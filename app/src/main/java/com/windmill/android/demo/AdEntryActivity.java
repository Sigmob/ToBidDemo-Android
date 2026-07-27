package com.windmill.android.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.windmill.android.demo.natives.NativeAdActivity;
import com.windmill.sdk.WindMillAd;

/**
 * SDK初始化后的广告入口页面
 * 展示：横幅广告、开屏广告、激励广告、插屏广告、原生广告、渠道SDK版本、基础工具
 */
public class AdEntryActivity extends AppCompatActivity {

    private static final String TAG = "AdEntryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_entry);

        // SDK状态提示
        TextView tvStatus = findViewById(R.id.tv_sdk_status);
        boolean isInit = WindMillAd.sharedAds().isInit();
        tvStatus.setText(isInit ? "SDK状态：已初始化" : "SDK状态：未初始化");

        // 广告入口
        bindButton(R.id.bt_banner, BannerActivity.class);
        bindButton(R.id.bt_splash, SplashAdActivity.class);
        bindButton(R.id.bt_reward, RewardVideoActivity.class);
        bindButton(R.id.bt_interstitial, InterstitialActivity.class);
        bindButton(R.id.bt_native, NativeAdActivity.class);

        // 工具入口
//        bindButton(R.id.bt_version, VersionActivity.class);
        bindButton(R.id.bt_device, DeviceActivity.class);
    }

    private void bindButton(@IdRes int id, final Class<?> clz) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 渠道版本和设备信息不需要SDK初始化
                if (clz == VersionActivity.class || clz == DeviceActivity.class) {
                    startActivity(new Intent(AdEntryActivity.this, clz));
                    return;
                }

                if (!WindMillAd.sharedAds().isInit()) {
                    Log.w(TAG, "SDK未初始化，无法加载广告");
                    return;
                }

                startActivity(new Intent(AdEntryActivity.this, clz));
            }
        });
    }
}
