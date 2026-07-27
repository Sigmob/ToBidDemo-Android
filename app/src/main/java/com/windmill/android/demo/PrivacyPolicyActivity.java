package com.windmill.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.windmill.android.demo.widget.PrivacyPolicyDialog;
import com.windmill.sdk.WMAdConfig;
import com.windmill.sdk.WMCustomController;
import com.windmill.sdk.WindMillAd;

public class PrivacyPolicyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

//        if (shouldShowPrivacyPolicy()) {
//            showPrivacyPolicyDialog();
//        } else {
//            GoSplashActivity();
//        }
        showPrivacyPolicyDialog();
    }

//    private void GoSplashActivity() {
//        startActivity(new Intent(PrivacyPolicyActivity.this, SplashActivity.class));
//        finish();
//    }

//    private boolean shouldShowPrivacyPolicy() {
//        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
//        return !prefs.getBoolean("privacy_policy_accepted", false);
//    }


    private void showPrivacyPolicyDialog() {
        PrivacyPolicyDialog dialog = new PrivacyPolicyDialog(this);
        dialog.setOnPrivacyResultListener(new PrivacyPolicyDialog.OnPrivacyResultListener() {
            @Override
            public void onAccept() {
                // 用户同意隐私政策
                savePrivacyPolicyAccepted();
                startActivity(new Intent(PrivacyPolicyActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onReject() {
                // 用户拒绝隐私政策
                Toast.makeText(PrivacyPolicyActivity.this, "您需要同意隐私政策才能使用本应用", Toast.LENGTH_LONG).show();
                finish();
                System.exit(0); // 退出应用
            }
        });
        dialog.show();
    }

    private void savePrivacyPolicyAccepted() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        prefs.edit().putBoolean("privacy_policy_accepted", true).apply();
    }

}