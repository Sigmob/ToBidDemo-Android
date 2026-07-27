package com.windmill.android.demo.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 隐私设置管理器，使用SharedPreferences持久化隐私开关状态
 */
public class PrivacySettingManager {

    private static final String PREFS_NAME = "privacy_settings";
    private static PrivacySettingManager instance;
    private final SharedPreferences prefs;

    // 隐私控制开关key
    private static final String KEY_ADULT = "isAdult";
    private static final String KEY_PERSONALIZED = "isPersonalizedOn";
    private static final String KEY_PROGRAMMATIC = "isProgrammaticRecommendOn";

    // 设备隐私信息控制开关key
    private static final String KEY_CAN_USE_LOCATION = "canUseLocation";
    private static final String KEY_CAN_USE_ANDROID_ID = "canUseAndroidId";
    private static final String KEY_CAN_USE_OAID = "canUseOaid";
    private static final String KEY_CAN_USE_APP_LIST = "canUseAppList";
    private static final String KEY_CAN_USE_WRITE_EXTERNAL = "canUseWriteExternal";
    private static final String KEY_CAN_USE_RECORD_AUDIO = "canUseRecordAudio";
    private static final String KEY_CAN_USE_SPACE_SIZE = "canUseSpaceSize";
    private static final String KEY_CAN_USE_SENSOR = "canUseSensor";
    private static final String KEY_CAN_USE_SIM_OPERATOR = "canUseSimOperator";

    private PrivacySettingManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PrivacySettingManager getInstance(Context context) {
        if (instance == null) {
            instance = new PrivacySettingManager(context);
        }
        return instance;
    }

    // ========== 隐私控制 ==========

    public boolean isAdult() {
        return prefs.getBoolean(KEY_ADULT, true);
    }

    public void setAdult(boolean value) {
        prefs.edit().putBoolean(KEY_ADULT, value).apply();
    }

    public boolean isPersonalizedOn() {
        return prefs.getBoolean(KEY_PERSONALIZED, true);
    }

    public void setPersonalizedOn(boolean value) {
        prefs.edit().putBoolean(KEY_PERSONALIZED, value).apply();
    }

    public boolean isProgrammaticRecommendOn() {
        return prefs.getBoolean(KEY_PROGRAMMATIC, true);
    }

    public void setProgrammaticRecommendOn(boolean value) {
        prefs.edit().putBoolean(KEY_PROGRAMMATIC, value).apply();
    }

    // ========== 设备隐私信息控制 ==========

    public boolean canUseLocation() {
        return prefs.getBoolean(KEY_CAN_USE_LOCATION, true);
    }

    public void setCanUseLocation(boolean value) {
        prefs.edit().putBoolean(KEY_CAN_USE_LOCATION, value).apply();
    }

    public boolean canUseAndroidId() {
        return prefs.getBoolean(KEY_CAN_USE_ANDROID_ID, true);
    }

    public void setCanUseAndroidId(boolean value) {
        prefs.edit().putBoolean(KEY_CAN_USE_ANDROID_ID, value).apply();
    }

    public boolean canUseOaid() {
        return prefs.getBoolean(KEY_CAN_USE_OAID, true);
    }

    public void setCanUseOaid(boolean value) {
        prefs.edit().putBoolean(KEY_CAN_USE_OAID, value).apply();
    }

    public boolean canUseAppList() {
        return prefs.getBoolean(KEY_CAN_USE_APP_LIST, true);
    }

    public void setCanUseAppList(boolean value) {
        prefs.edit().putBoolean(KEY_CAN_USE_APP_LIST, value).apply();
    }

    public boolean canUseWriteExternal() {
        return prefs.getBoolean(KEY_CAN_USE_WRITE_EXTERNAL, true);
    }

    public void setCanUseWriteExternal(boolean value) {
        prefs.edit().putBoolean(KEY_CAN_USE_WRITE_EXTERNAL, value).apply();
    }

    public boolean canUseRecordAudio() {
        return prefs.getBoolean(KEY_CAN_USE_RECORD_AUDIO, true);
    }

    public void setCanUseRecordAudio(boolean value) {
        prefs.edit().putBoolean(KEY_CAN_USE_RECORD_AUDIO, value).apply();
    }

    public boolean canUseSpaceSize() {
        return prefs.getBoolean(KEY_CAN_USE_SPACE_SIZE, true);
    }

    public void setCanUseSpaceSize(boolean value) {
        prefs.edit().putBoolean(KEY_CAN_USE_SPACE_SIZE, value).apply();
    }

    public boolean canUseSensor() {
        return prefs.getBoolean(KEY_CAN_USE_SENSOR, true);
    }

    public void setCanUseSensor(boolean value) {
        prefs.edit().putBoolean(KEY_CAN_USE_SENSOR, value).apply();
    }

    public boolean canUseSimOperator() {
        return prefs.getBoolean(KEY_CAN_USE_SIM_OPERATOR, true);
    }

    public void setCanUseSimOperator(boolean value) {
        prefs.edit().putBoolean(KEY_CAN_USE_SIM_OPERATOR, value).apply();
    }
}
