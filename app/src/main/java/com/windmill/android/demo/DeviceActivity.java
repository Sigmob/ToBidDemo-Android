package com.windmill.android.demo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.czhj.sdk.common.ClientMetadata;
import com.windmill.android.demo.manager.PrivacySettingManager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基础工具页面
 * 展示已获取的设备ID及已授权的权限信息
 */
public class DeviceActivity extends Activity {

    private TableLayout tlDeviceId;
    private TableLayout tlPermission;

    private Map<String, String> mDeviceIds = new LinkedHashMap<>();
    private Map<String, String> mPermissions = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        tlDeviceId = findViewById(R.id.tl_device_id);
        tlPermission = findViewById(R.id.tl_permission);

        initDeviceIds();
        initPermissionInfo();
        renderDeviceIdTable();
        renderPermissionTable();
    }

    public int dipsToIntPixels(int dips) {
        float density = this.getResources().getDisplayMetrics().density;
        return (int) ((dips * density) + 0.5f);
    }

    private void initDeviceIds() {
        mDeviceIds.clear();
        mDeviceIds.put("AndroidID", getAndroidId());
        mDeviceIds.put("OAID", safeGet(new StringSupplier() {
            @Override
            public String get() {
                return ClientMetadata.getInstance().getOAID();
            }
        }));
        mDeviceIds.put("UDID", safeGet(new StringSupplier() {
            @Override
            public String get() {
                return ClientMetadata.getInstance().getUDID();
            }
        }));
        mDeviceIds.put("UID", safeGet(new StringSupplier() {
            @Override
            public String get() {
                return ClientMetadata.getUid();
            }
        }));
    }

    private String safeGet(StringSupplier supplier) {
        try {
            String result = supplier.get();
            return (result != null && !result.isEmpty()) ? result : "未获取";
        } catch (Exception e) {
            return "获取失败";
        }
    }

    private interface StringSupplier {
        String get() throws Exception;
    }

    private String getAndroidId() {
        try {
            return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            return "获取失败";
        }
    }

    private void initPermissionInfo() {
        mPermissions.clear();
        PrivacySettingManager settings = PrivacySettingManager.getInstance(this);

        mPermissions.put("地理位置", settings.canUseLocation() ? "已授权" : "未授权");
        mPermissions.put("AndroidId", settings.canUseAndroidId() ? "已授权" : "未授权");
        mPermissions.put("OAID", settings.canUseOaid() ? "已授权" : "未授权");
        mPermissions.put("应用安装列表", settings.canUseAppList() ? "已授权" : "未授权");
        mPermissions.put("写入外部存储", settings.canUseWriteExternal() ? "已授权" : "未授权");
        mPermissions.put("录音权限", settings.canUseRecordAudio() ? "已授权" : "未授权");
        mPermissions.put("存储空间信息", settings.canUseSpaceSize() ? "已授权" : "未授权");
        mPermissions.put("传感器", settings.canUseSensor() ? "已授权" : "未授权");
        mPermissions.put("运营商信息", settings.canUseSimOperator() ? "已授权" : "未授权");

        // 系统运行时权限状态
        mPermissions.put("INTERNET", checkPermission(Manifest.permission.INTERNET));
        mPermissions.put("ACCESS_NETWORK_STATE", checkPermission(Manifest.permission.ACCESS_NETWORK_STATE));
        mPermissions.put("ACCESS_WIFI_STATE", checkPermission(Manifest.permission.ACCESS_WIFI_STATE));
    }

    private String checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED ? "已授权" : "未授权";
        }
        return "已授权";
    }

    private void renderDeviceIdTable() {
        tlDeviceId.removeAllViews();
        addHeaderRow(tlDeviceId, "设备ID类型", "值");
        for (Map.Entry<String, String> entry : mDeviceIds.entrySet()) {
            addDataRow(tlDeviceId, entry.getKey(), entry.getValue());
        }
    }

    private void renderPermissionTable() {
        tlPermission.removeAllViews();
        addHeaderRow(tlPermission, "权限名称", "状态");
        for (Map.Entry<String, String> entry : mPermissions.entrySet()) {
            addDataRow(tlPermission, entry.getKey(), entry.getValue());
        }
    }

    private void addHeaderRow(TableLayout table, String col1, String col2) {
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
        tv1.setText(col1);
        tv1.setTextSize(14);
        row.addView(tv1);

        TextView tv2 = new TextView(this);
        TableRow.LayoutParams params2 = new TableRow.LayoutParams(0, dipsToIntPixels(40));
        params2.weight = 3;
        tv2.setLayoutParams(params2);
        tv2.setGravity(Gravity.CENTER);
        tv2.setBackgroundColor(Color.WHITE);
        tv2.setText(col2);
        tv2.setTextSize(14);
        row.addView(tv2);

        table.addView(row);
    }

    private void addDataRow(TableLayout table, String col1, String col2) {
        TableRow row = new TableRow(this);
        row.setBackgroundColor(Color.GRAY);
        row.setPadding(1, 0, 1, 1);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView tv1 = new TextView(this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, dipsToIntPixels(40));
        params1.weight = 1;
        params1.setMargins(0, 0, 1, 0);
        tv1.setLayoutParams(params1);
        tv1.setGravity(Gravity.CENTER);
        tv1.setBackgroundColor(Color.WHITE);
        tv1.setText(col1);
        tv1.setTextSize(13);
        row.addView(tv1);

        final TextView tv2 = new TextView(this);
        TableRow.LayoutParams params2 = new TableRow.LayoutParams(0, dipsToIntPixels(40));
        params2.weight = 3;
        tv2.setLayoutParams(params2);
        tv2.setGravity(Gravity.CENTER);
        tv2.setBackgroundColor(Color.WHITE);
        tv2.post(new Runnable() {
            @Override
            public void run() {
                tv2.setTextIsSelectable(true);
            }
        });
        tv2.setText(col2);
        tv2.setTextSize(13);
        row.addView(tv2);

        table.addView(row);
    }
}
