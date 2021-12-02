package com.windmill.android.demo;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.bugly.crashreport.CrashReport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private MainFragment mMainFragment;
    private Map<String, String> mAdVersions = new HashMap();
    private String[] mAdNames = {"WindMill", "Vungle", "OneWay", "Mobvista", "Tapjoy", "UnityAd", "头条", "快手", "广点通"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createMainFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(READ_PHONE_STATE) != PERMISSION_GRANTED
                    || checkSelfPermission(ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                    || checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {

                requestPermissions(new String[]{READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION}, PERMISSION_GRANTED);

            } else if (checkSelfPermission(READ_PHONE_STATE) != PERMISSION_GRANTED) {

                requestPermissions(new String[]{READ_PHONE_STATE}, PERMISSION_GRANTED);

            } else if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                // TODO: Consider calling
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_GRANTED);
            }
        }

        CrashReport.initCrashReport(getApplicationContext(), "4c41e5eed0", true);
//        CrashReport.testJavaCrash();
        initChannelVersion();
    }

    private void initChannelVersion() {
        mAdVersions.clear();
        for (int i = 0; i < mAdNames.length; i++) {
            String mAdName = mAdNames[i];
            switch (mAdName) {
                case "WindMill":
                    try {
                        Class aClass = Class.forName("com.windmill.sdk.WindMillAd");
                        Method method = aClass.getMethod("getVersion");
                        method.setAccessible(true);
                        String invoke = (String) method.invoke(aClass);
                        mAdVersions.put(mAdName, invoke);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "NoChannel");
                        e.printStackTrace();
                    }
                    break;
                case "Vungle":
                    try {
                        Class cls = Class.forName("com.vungle.warren.BuildConfig");
                        Object obj = cls.newInstance();
                        Field f = cls.getDeclaredField("VERSION_NAME");
                        f.setAccessible(true);
                        String o = (String) f.get(obj);
                        mAdVersions.put(mAdName, o);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "NoChannel");
                        e.printStackTrace();
                    }
                    break;
                case "OneWay":
                    try {
                        Class aClass = Class.forName("mobi.oneway.export.Ad.OnewaySdk");
                        Method method = aClass.getMethod("getVersion");
                        method.setAccessible(true);
                        String invoke = (String) method.invoke(aClass);
                        mAdVersions.put(mAdName, invoke);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "NoChannel");
                        e.printStackTrace();
                    }
                    break;
                case "Mobvista":
                    try {
                        Class cls = Class.forName("com.mintegral.msdk.out.MTGConfiguration");
                        Object obj = cls.newInstance();
                        Field f = cls.getDeclaredField("SDK_VERSION");
                        f.setAccessible(true);
                        String o = (String) f.get(obj);
                        mAdVersions.put(mAdName, o);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "NoChannel");
                        e.printStackTrace();
                    }
                    break;
                case "Tapjoy":
                    try {
                        Class cls = Class.forName("com.tapjoy.BuildConfig");
                        Object obj = cls.newInstance();
                        Field f = cls.getDeclaredField("VERSION_NAME");
                        f.setAccessible(true);
                        String o = (String) f.get(obj);
                        mAdVersions.put(mAdName, o);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "NoChannel");
                        e.printStackTrace();
                    }
                    break;
                case "UnityAd":
                    try {
                        Class cls = Class.forName("com.unity3d.ads.BuildConfig");
                        Object obj = cls.newInstance();
                        Field f = cls.getDeclaredField("VERSION_NAME");
                        f.setAccessible(true);
                        String o = (String) f.get(obj);
                        mAdVersions.put(mAdName, o);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "NoChannel");
                        e.printStackTrace();
                    }
                    break;
                case "头条":
                    try {
                        Class ttAdSdk = Class.forName("com.bytedance.sdk.openadsdk.TTAdSdk");
                        Method getAdManager = ttAdSdk.getMethod("getAdManager");
                        getAdManager.setAccessible(true);
                        Object ttAdManager = getAdManager.invoke(ttAdSdk);
                        Class<?> aClass = ttAdManager.getClass();
                        Method getSDKVersion = aClass.getMethod("getSDKVersion");
                        getSDKVersion.setAccessible(true);
                        String invoke = (String) getSDKVersion.invoke(ttAdManager);
                        mAdVersions.put(mAdName, invoke);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "NoChannel");
                        e.printStackTrace();
                    }
                    break;
                case "快手":
                    try {
                        Class aClass = Class.forName("com.kwad.sdk.api.KsAdSDK");
                        Method method = aClass.getMethod("getSDKVersion");
                        method.setAccessible(true);
                        String invoke = (String) method.invoke(aClass);
                        mAdVersions.put(mAdName, invoke);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "NoChannel");
                        e.printStackTrace();
                    }
                    break;
                case "广点通":
                    try {
                        Class aClass = Class.forName("com.qq.e.comm.managers.status.SDKStatus");
                        Method method = aClass.getMethod("getIntegrationSDKVersion");
                        method.setAccessible(true);
                        String invoke = (String) method.invoke(aClass);
                        mAdVersions.put(mAdName, invoke);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "NoChannel");
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mMainFragment != null && intent != null) {
            String[] logs = intent.getStringArrayExtra("logs");
            mMainFragment.setLogs(logs);
        }
    }

    private void createMainFragment() {

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.fragment_container) != null && mMainFragment == null) {

            mMainFragment = new MainFragment();
            Intent intent = getIntent();
            String[] logs = intent.getStringArrayExtra("logs");

            mMainFragment.setLogs(logs);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, mMainFragment).commit();
        }
    }

    //调用菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        for (Map.Entry<String, String> entry : mAdVersions.entrySet()) {
            menu.add(entry.getKey() + "-" + entry.getValue());
        }

        return true;
    }


    @Override
    public void onBackPressed() {

        Log.d(TAG, "onBackPressed() called " + getFragmentManager().getBackStackEntryCount());
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

}