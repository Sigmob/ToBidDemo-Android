package com.windmill.android.demo;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

//import com.tencent.bugly.crashreport.CrashReport;

/**
 * created by lance on   2021/12/7 : 1:26 下午
 */
public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
//        CrashReport.initCrashReport(getApplicationContext(), "4c41e5eed0", true);//4c41e5eed0//4ee13aff7b
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
