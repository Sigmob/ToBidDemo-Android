package com.windmill.android.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.windmill.android.demo.manager.SdkInitManager;
import com.windmill.sdk.WindMillAd;


/**
 * Demo首页
 * 四个入口：隐私设置、隐私设备信息控制、SDK初始化、个人信息查询
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_PHONE_STATE = 1001;
    private static final int REQUEST_CODE_LOCATION = 1002;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 隐私设置
        findViewById(R.id.bt_privacy_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PrivacySettingActivity.class));
            }
        });

        // 隐私设备信息控制
        findViewById(R.id.bt_device_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DeviceSettingActivity.class));
            }
        });

        // 申请手机状态权限
        findViewById(R.id.bt_permission_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPhoneStatePermission();
            }
        });

        // 申请位置权限
        findViewById(R.id.bt_permission_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationPermission();
            }
        });

        // SDK初始化
        findViewById(R.id.bt_sdk_init).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WindMillAd.sharedAds().isInit()) {
                    Toast.makeText(MainActivity.this, "SDK已初始化", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, AdEntryActivity.class));
                    return;
                }
                SdkInitManager.initSDK(MainActivity.this, new WindMillAd.WindMillAdInitListener() {
                    @Override
                    public void onInitSuccess() {
                        Log.d(TAG, "------onInitSuccess------");
                        Toast.makeText(MainActivity.this, "初始化成功", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, AdEntryActivity.class));
                    }

                    @Override
                    public void onInitFailed(int i, String s) {
                        Log.d(TAG, "------onInitFailed------" + i + s);
                        Toast.makeText(MainActivity.this, "初始化失败：" + s, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // 个人信息查询
        findViewById(R.id.bt_privacy_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindMillAd.sharedAds().openPrivacyView(MainActivity.this);
            }
        });
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

    private void requestPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_CODE_PHONE_STATE);
            } else {
                Toast.makeText(this, "手机状态权限已授权", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "当前系统版本无需动态申请", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_LOCATION);
            } else {
                Toast.makeText(this, "位置权限已授权", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "当前系统版本无需动态申请", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) return;
        switch (requestCode) {
            case REQUEST_CODE_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "手机状态权限已授权", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "手机状态权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "位置权限已授权", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "位置权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
