package com.windmill.android.demo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class Constants {

    public static final String CONF_APPID = "conf_appId";
    public static final String CONF_APPKEY = "conf_appKey";
    public static final String CONF_SPLASH_PLACEMENTID = "conf_splash_placementId";
    public static final String CONF_REWARD_PLACEMENTID = "conf_reward_placementId";
    public static final String CONF_FULLSCREEN_PLACEMENTID = "conf_fullScreen_placementId";
    public static final String CONF_UNIFIED_NATIVE_PLACEMENTID = "conf_unified_native_placementId";
    public static final String CONF_USERID = "conf_userId";
    public static final String CONF_APP_TITLE = "conf_appTitle";
    public static final String CONF_APP_DESC = "conf_appDesc";
    public static final String CONF_HALF_SPLASH = "conf_half_splash";

    public static String app_id = null;
    public static String app_key = null;
    public static String reward_placement_id = null;
    public static String splash_placement_id = null;
    public static String fullScreen_placement_id = null;
    public static String native_unified_placement_id = null;

    public static void loadDefualtAdSetting(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getApplicationInfo().packageName, PackageManager.GET_META_DATA);
            app_id = String.valueOf(appInfo.metaData.get("sigmob.app_id"));
            app_key = appInfo.metaData.getString("sigmob.app_key");
            reward_placement_id = appInfo.metaData.getString("sigmob.reward_placement_id");
            splash_placement_id = appInfo.metaData.getString("sigmob.splash_placement_id");
            fullScreen_placement_id = appInfo.metaData.getString("sigmob.fullScreen_placement_id");
            native_unified_placement_id = appInfo.metaData.getString("sigmob.native_unified_placement_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
