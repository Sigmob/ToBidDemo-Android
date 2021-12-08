package com.windmill.android.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class DeviceActivity extends Activity {

    private TableLayout tl;

    private Map<String, String> mAdVersions = new LinkedHashMap<>();

    private String[] mAdNames = {"SigId", "GAID", "OAID", "IMEI"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
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

            for (Map.Entry<String, String> entry : mAdVersions.entrySet()) {

                String key = entry.getKey();
                String value = entry.getValue();

                TableRow tb = new TableRow(this);
                tb.setBackgroundColor(Color.GRAY);
                tb.setPadding(1, 1, 1, 1);
                tb.setGravity(Gravity.CENTER_VERTICAL);

                final TextView tv3 = new TextView(this);
                TableRow.LayoutParams params3 = new TableRow.LayoutParams(0, dipsToIntPixels(50));
                params3.weight = 1;
                params3.setMargins(0, 0, 1, 0);
                tv3.setLayoutParams(params3);
                tv3.setGravity(Gravity.CENTER);
                tv3.setBackgroundColor(Color.WHITE);
                tv3.setText(key);
                tb.addView(tv3);

                final TextView tv4 = new TextView(this);
                TableRow.LayoutParams params4 = new TableRow.LayoutParams(0, dipsToIntPixels(50));
                params4.weight = 4;
                tv4.setLayoutParams(params4);
                tv4.setGravity(Gravity.CENTER);
                tv4.setBackgroundColor(Color.WHITE);
                tv4.post(new Runnable() {
                    @Override
                    public void run() {
                        tv4.setTextIsSelectable(true);
                    }
                });
                tv4.setText(value);
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
                case "SigId":
                    try {
                        Class cm = Class.forName("com.czhj.sdk.common.ClientMetadata");
                        Method getInstance = cm.getMethod("getInstance");
                        getInstance.setAccessible(true);
                        Object instance = getInstance.invoke(cm);
                        Class<?> aClass = instance.getClass();
                        Method deviceId = aClass.getMethod("getUid");
                        deviceId.setAccessible(true);
                        String invoke = (String) deviceId.invoke(instance);
                        if (TextUtils.isEmpty(invoke)) {
                            mAdVersions.put(mAdName, "Null");
                        } else {
                            mAdVersions.put(mAdName, invoke);
                        }
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "Null");
                        e.printStackTrace();
                    }
                    break;
                case "IMEI":
                    try {
                        Class cm = Class.forName("com.czhj.sdk.common.ClientMetadata");
                        Method getInstance = cm.getMethod("getInstance");
                        getInstance.setAccessible(true);
                        Object instance = getInstance.invoke(cm);
                        Class<?> aClass = instance.getClass();
                        Method deviceId = aClass.getMethod("getDeviceId");
                        deviceId.setAccessible(true);
                        String invoke = (String) deviceId.invoke(instance);
                        if (TextUtils.isEmpty(invoke)) {
                            mAdVersions.put(mAdName, "Null");
                        } else {
                            mAdVersions.put(mAdName, invoke);
                        }
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "Null");
                        e.printStackTrace();
                    }
                    break;
                case "OAID":
                    try {
                        Class cm = Class.forName("com.czhj.sdk.common.ClientMetadata");
                        Method getInstance = cm.getMethod("getInstance");
                        getInstance.setAccessible(true);
                        Object instance = getInstance.invoke(cm);
                        Class<?> aClass = instance.getClass();
                        Method deviceId = aClass.getMethod("getOAID");
                        deviceId.setAccessible(true);
                        String invoke = (String) deviceId.invoke(instance);
                        if (TextUtils.isEmpty(invoke)) {
                            mAdVersions.put(mAdName, "Null");
                        } else {
                            mAdVersions.put(mAdName, invoke);
                        }
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "Null");
                        e.printStackTrace();
                    }
                    break;
                case "GAID":
                    try {
                        Class cm = Class.forName("com.czhj.sdk.common.ClientMetadata");
                        Method getInstance = cm.getMethod("getInstance");
                        getInstance.setAccessible(true);
                        Object instance = getInstance.invoke(cm);
                        Class<?> aClass = instance.getClass();
                        Method deviceId = aClass.getMethod("getAdvertisingId");
                        deviceId.setAccessible(true);
                        String invoke = (String) deviceId.invoke(instance);
                        if (TextUtils.isEmpty(invoke)) {
                            mAdVersions.put(mAdName, "Null");
                        } else {
                            mAdVersions.put(mAdName, invoke);
                        }
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "Null");
                        e.printStackTrace();
                    }
                    break;

            }
        }
    }

}