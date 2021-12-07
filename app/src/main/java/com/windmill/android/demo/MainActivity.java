package com.windmill.android.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.windmill.android.demo.natives.NativeAdActivity;
import com.windmill.sdk.WindMillAd;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindMillAd.requestPermission(this);
        bindButton(R.id.bt_reward, RewardVideoActivity.class);
        bindButton(R.id.bt_interstitial, InterstitialActivity.class);
        bindButton(R.id.bt_splash, SplashAdActivity.class);
        bindButton(R.id.bt_native, NativeAdActivity.class);
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