package com.windmill.android.demo;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.windmill.android.demo.manager.PrivacySettingManager;
import com.windmill.sdk.WindMillAd;

/**
 * 隐私设置页面
 * 参考 https://doc.sigmob.com/tobid/21161/ 文档实现
 * 包含：未成年控制、个性化推荐、程序化推荐
 */
public class PrivacySettingActivity extends AppCompatActivity {

    private SwitchCompat switchAdult;
    private SwitchCompat switchPersonalized;
    private SwitchCompat switchProgrammatic;

    private PrivacySettingManager settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_setting);

        settings = PrivacySettingManager.getInstance(this);

        initViews();
        loadSettings();
    }

    private void initViews() {
        switchAdult = findViewById(R.id.switch_adult);
        switchPersonalized = findViewById(R.id.switch_personalized);
        switchProgrammatic = findViewById(R.id.switch_programmatic);

        switchAdult.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setAdult(isChecked);
                WindMillAd.sharedAds().setAdult(isChecked);
                showToast("是否成年人: " + (isChecked ? "成年" : "未成年"));
            }
        });

        switchPersonalized.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setPersonalizedOn(isChecked);
                WindMillAd.sharedAds().setPersonalizedAdvertisingOn(isChecked);
                showToast("是否开启个性化推荐: " + (isChecked ? "开启" : "关闭"));
            }
        });

        switchProgrammatic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setProgrammaticRecommendOn(isChecked);
                try {
                    WindMillAd.sharedAds().getClass()
                            .getMethod("setProgrammaticRecommendOn", boolean.class)
                            .invoke(WindMillAd.sharedAds(), isChecked);
                } catch (Exception e) {
                    // SDK版本可能不支持此方法
                }
                showToast("是否开启程序化推荐: " + (isChecked ? "开启" : "关闭"));
            }
        });
    }

    private void loadSettings() {
        switchAdult.setChecked(settings.isAdult());
        switchPersonalized.setChecked(settings.isPersonalizedOn());
        switchProgrammatic.setChecked(settings.isProgrammaticRecommendOn());
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
