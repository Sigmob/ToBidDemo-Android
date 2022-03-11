package com.windmill.android.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class VersionActivity extends Activity {

    private TableLayout tl;

    private Map<String, String> mAdVersions = new LinkedHashMap<>();

    private String[] mAdNames = {"WindMill", "Sigmob", "Vungle", "Mintegral", "UnityAds", "穿山甲", "快手", "腾讯优量汇", "游可赢", "百度"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        tl = findViewById(R.id.tl_version);
        initChannelVersion();
        createView();
    }

    public int dipsToIntPixels(int dips) {
        float density = this.getResources().getDisplayMetrics().density;
        return (int) ((dips * density) + 0.5f);
    }


    private void createView() {
        tl.removeAllViews();
        if (mAdVersions.size() > 0) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(Color.GRAY);
            row.setPadding(1, 1, 1, 1);
            row.setGravity(Gravity.CENTER_VERTICAL);

            TextView tv1 = new TextView(this);
            TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, dipsToIntPixels(40));
            params1.weight = 1;
            params1.setMargins(0, 0, 1, 0);
            tv1.setLayoutParams(params1);
            tv1.setGravity(Gravity.CENTER);
            tv1.setBackgroundColor(Color.WHITE);
            tv1.setText("渠道");
            tv1.setTextSize(15);
            row.addView(tv1);

            TextView tv2 = new TextView(this);
            TableRow.LayoutParams params2 = new TableRow.LayoutParams(0, dipsToIntPixels(40));
            params2.weight = 1;
            tv2.setLayoutParams(params2);
            tv2.setGravity(Gravity.CENTER);
            tv2.setBackgroundColor(Color.WHITE);
            tv2.setText("SDK版本");
            tv2.setTextSize(15);
            row.addView(tv2);

            tl.addView(row);


            for (Map.Entry<String, String> entry : mAdVersions.entrySet()) {

                String key = entry.getKey();
                String value = entry.getValue();

                TableRow tb = new TableRow(this);
                tb.setBackgroundColor(Color.GRAY);
                tb.setPadding(1, 0, 1, 1);
                tb.setGravity(Gravity.CENTER_VERTICAL);

                TextView tv3 = new TextView(this);
                TableRow.LayoutParams params3 = new TableRow.LayoutParams(0, dipsToIntPixels(40));
                params3.weight = 1;
                params3.setMargins(0, 0, 1, 0);
                tv3.setLayoutParams(params3);
                tv3.setGravity(Gravity.CENTER);
                tv3.setBackgroundColor(Color.WHITE);
                tv3.setText(key);
                tv3.setTextSize(14);
                tb.addView(tv3);

                TextView tv4 = new TextView(this);
                TableRow.LayoutParams params4 = new TableRow.LayoutParams(0, dipsToIntPixels(40));
                params4.weight = 1;
                tv4.setLayoutParams(params4);
                tv4.setGravity(Gravity.CENTER);
                tv4.setBackgroundColor(Color.WHITE);
                tv4.setText(value);
                tv4.setTextSize(14);
                tb.addView(tv4);

                tl.addView(tb);
            }
        }
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
                case "Sigmob":
                    try {
                        Class aClass = Class.forName("com.sigmob.windad.WindAds");
                        Method method = aClass.getMethod("getVersion");
                        method.setAccessible(true);
                        String invoke = (String) method.invoke(aClass);
                        mAdVersions.put(mAdName, invoke);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "NoChannel");
                        e.printStackTrace();
                    }
                    break;
                case "游可赢":
                    try {
                        Class aClass = Class.forName("com.tencent.klevin.KlevinManager");
                        Method method = aClass.getMethod("getVersion");
                        method.setAccessible(true);
                        String invoke = (String) method.invoke(aClass);
                        mAdVersions.put(mAdName, invoke);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "NoChannel");
                        e.printStackTrace();
                    }
                    break;
                case "百度":
                    try {
                        Class aClass = Class.forName("com.baidu.mobads.sdk.api.AdSettings");
                        Method method = aClass.getMethod("getSDKVersion");
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
//                case "OneWay":
//                    try {
//                        Class aClass = Class.forName("mobi.oneway.export.Ad.OnewaySdk");
//                        Method method = aClass.getMethod("getVersion");
//                        method.setAccessible(true);
//                        String invoke = (String) method.invoke(aClass);
//                        mAdVersions.put(mAdName, invoke);
//                    } catch (Exception e) {
//                        mAdVersions.put(mAdName, "NoChannel");
//                        e.printStackTrace();
//                    }
//                    break;
                case "Mintegral":
                    try {
                        Class cls = Class.forName("com.mbridge.msdk.out.MBConfiguration");
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
//                case "Tapjoy":
//                    try {
//                        Class cls = Class.forName("com.tapjoy.BuildConfig");
//                        Object obj = cls.newInstance();
//                        Field f = cls.getDeclaredField("VERSION_NAME");
//                        f.setAccessible(true);
//                        String o = (String) f.get(obj);
//                        mAdVersions.put(mAdName, o);
//                    } catch (Exception e) {
//                        mAdVersions.put(mAdName, "NoChannel");
//                        e.printStackTrace();
//                    }
//                    break;
                case "UnityAds":
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
                case "穿山甲":
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
                case "腾讯优量会":
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
}