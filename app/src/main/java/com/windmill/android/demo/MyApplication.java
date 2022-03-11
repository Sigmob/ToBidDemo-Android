package com.windmill.android.demo;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.tencent.bugly.crashreport.CrashReport;
import com.windmill.sdk.WindMillAd;
import com.windmill.sdk.WindMillConsentStatus;
import com.windmill.sdk.WindMillOptions;
import com.windmill.sdk.WindMillUserAgeStatus;

/**
 * created by lance on   2021/12/7 : 1:26 下午
 */
public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        CrashReport.initCrashReport(getApplicationContext(), "4c41e5eed0", true);//4c41e5eed0//4ee13aff7b

        initSDK();
    }

    private void initSDK() {

        WindMillAd ads = WindMillAd.sharedAds();

        ads.setUserAge(18);
        ads.setAdult(true);//是否成年
        ads.setPersonalizedAdvertisingOn(true);//是否开启个性化推荐接口
        ads.setIsAgeRestrictedUser(WindMillUserAgeStatus.WindAgeRestrictedStatusNO);//coppa//是否年龄限制
        ads.setUserGDPRConsentStatus(WindMillConsentStatus.ACCEPT);//是否接受gdpr协议

        ads.startWithOptions(this, new WindMillOptions("16991", "1c6d03bc081fbb82"));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);

    }
}
