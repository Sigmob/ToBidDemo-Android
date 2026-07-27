package com.windmill.android.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.windmill.android.demo.manager.PrivacySettingManager;

/**
 * 设备隐私信息控制页面
 * 根据文档中“设备隐私信息控制设置”的开关选项进行控制
 * 开关状态将在SDK初始化时生效
 */
public class DeviceSettingActivity extends AppCompatActivity {

    private PrivacySettingManager settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);

        settings = PrivacySettingManager.getInstance(this);

        LinearLayout container = findViewById(R.id.container_device_privacy);
        LayoutInflater inflater = LayoutInflater.from(this);

        addSwitchItem(inflater, container, "地理位置", "允许SDK主动使用地理位置信息",
                settings.canUseLocation(), new SwitchCallback() {
                    @Override
                    public void onChanged(boolean value) {
                        settings.setCanUseLocation(value);
                    }
                });

        addSwitchItem(inflater, container, "AndroidId", "允许SDK主动使用AndroidId",
                settings.canUseAndroidId(), new SwitchCallback() {
                    @Override
                    public void onChanged(boolean value) {
                        settings.setCanUseAndroidId(value);
                    }
                });

        addSwitchItem(inflater, container, "OAID", "允许SDK获取OAID",
                settings.canUseOaid(), new SwitchCallback() {
                    @Override
                    public void onChanged(boolean value) {
                        settings.setCanUseOaid(value);
                    }
                });

        addSwitchItem(inflater, container, "应用安装列表", "允许SDK获取设备上应用安装列表",
                settings.canUseAppList(), new SwitchCallback() {
                    @Override
                    public void onChanged(boolean value) {
                        settings.setCanUseAppList(value);
                    }
                });

        addSwitchItem(inflater, container, "写入外部存储", "允许SDK主动使用WRITE_EXTERNAL_STORAGE权限",
                settings.canUseWriteExternal(), new SwitchCallback() {
                    @Override
                    public void onChanged(boolean value) {
                        settings.setCanUseWriteExternal(value);
                    }
                });

        addSwitchItem(inflater, container, "录音权限", "允许SDK在授权后使用录音权限",
                settings.canUseRecordAudio(), new SwitchCallback() {
                    @Override
                    public void onChanged(boolean value) {
                        settings.setCanUseRecordAudio(value);
                    }
                });

        addSwitchItem(inflater, container, "存储空间信息", "允许SDK获取存储空间总大小及可用空间",
                settings.canUseSpaceSize(), new SwitchCallback() {
                    @Override
                    public void onChanged(boolean value) {
                        settings.setCanUseSpaceSize(value);
                    }
                });

        addSwitchItem(inflater, container, "传感器", "允许SDK主动获取设备传感器信息",
                settings.canUseSensor(), new SwitchCallback() {
                    @Override
                    public void onChanged(boolean value) {
                        settings.setCanUseSensor(value);
                    }
                });

        addSwitchItem(inflater, container, "运营商信息", "允许SDK主动查询运营商信息",
                settings.canUseSimOperator(), new SwitchCallback() {
                    @Override
                    public void onChanged(boolean value) {
                        settings.setCanUseSimOperator(value);
                    }
                });
    }

    private interface SwitchCallback {
        void onChanged(boolean value);
    }

    private void addSwitchItem(LayoutInflater inflater, LinearLayout container,
                               String title, String desc, boolean checked,
                               final SwitchCallback callback) {
        View itemView = inflater.inflate(R.layout.item_device_privacy_switch, container, false);
        TextView tvTitle = itemView.findViewById(R.id.tv_title);
        TextView tvDesc = itemView.findViewById(R.id.tv_desc);
        SwitchCompat switchCompat = itemView.findViewById(R.id.switch_control);

        tvTitle.setText(title);
        tvDesc.setText(desc);
        switchCompat.setChecked(checked);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                callback.onChanged(isChecked);
                showToast(title + ": " + (isChecked ? "允许" : "禁止"));
            }
        });

        container.addView(itemView);

        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(0xFFF0F0F0);
        container.addView(divider);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
