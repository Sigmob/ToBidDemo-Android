package com.windmill.android.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.tencent.bugly.crashreport.CrashReport;
import com.windmill.sdk.WMAdConfig;
import com.windmill.sdk.WMAdnInitConfig;
import com.windmill.sdk.WMCustomController;
import com.windmill.sdk.WMNetworkConfig;
import com.windmill.sdk.WindMillAd;
import com.windmill.sdk.WindMillConsentStatus;
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


        /**
         * 前置初始化第三方sdk
         */
//        WMNetworkConfig.Builder builder = (new WMNetworkConfig.Builder())
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.ADMOB))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.REKLAMUP))
//                .addInitConfig(new WMAdnInitConfig(23, "appId"))
//                .addInitConfig(new WMAdnInitConfig(24, "appId"))
//                .addInitConfig(new WMAdnInitConfig(25, "appId"))
//                .addInitConfig(new WMAdnInitConfig(26, "appId"))//异步
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.VUNGLE, "appId"))//异步
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.UNITYADS, "appId"))//异步
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.IRONSOURCE, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.TOUTIAO, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.GDT, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.KUAISHOU, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.KLEVIN, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.BAIDU, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.GROMORE, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.ADSCOPE, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.QUMENG, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.PANGLE, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.APPLOVIN, "appKey"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.APPLOVIN_MAX, "appKey"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.MOBVISTA, "appId", "appKey"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.SIGMOB, "appId", "appKey"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.TAPTAP, "appId", "appKey"));
//        ads.setInitNetworkConfig(builder.build());

        ads.startWithAppId(this, "16991", new WMAdConfig.Builder().customController(new WMCustomController() {
            @Override
            public boolean isCanUseLocation() {
                return super.isCanUseLocation();
            }

            @Override
            public Location getLocation() {
                return super.getLocation();
            }

            @Override
            public boolean isCanUsePhoneState() {
                return super.isCanUsePhoneState();
            }

            @Override
            public String getDevImei() {
                return super.getDevImei();
            }

            @Override
            public boolean isCanUseAndroidId() {
                return super.isCanUseAndroidId();
            }

            @Override
            public String getAndroidId() {
                return super.getAndroidId();
            }

            @Override
            public String getDevOaid() {
                return super.getDevOaid();
            }

            @Override
            public boolean isCanUseWifiState() {
                return super.isCanUseWifiState();
            }

            @Override
            public String getMacAddress() {
                return super.getMacAddress();
            }

            @Override
            public boolean isCanUseWriteExternal() {
                return super.isCanUseWriteExternal();
            }

            @Override
            public boolean isCanUseAppList() {
                return super.isCanUseAppList();
            }

            @Override
            public boolean isCanUsePermissionRecordAudio() {
                return super.isCanUsePermissionRecordAudio();
            }
        }).build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);

    }

    @SuppressLint("MissingPermission")
    private Location getAppLocation() {
        Location lastLocation = null;

        try {
            if (this.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED
                    || this.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                // Get lat, long failFrom any GPS information that might be currently
                // available
                LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                for (String provider_name : lm.getProviders(true)) {
                    Location l = lm.getLastKnownLocation(provider_name);
                    if (l == null) {
                        continue;
                    }

                    if (lastLocation == null) {
                        lastLocation = l;
                    } else {
                        if (l.getTime() > 0 && lastLocation.getTime() > 0) {
                            if (l.getTime() > lastLocation.getTime()) {
                                lastLocation = l;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastLocation;
    }
}
