package com.windmill.android.demo.natives;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IdRes;

import com.windmill.android.demo.R;
import com.windmill.android.demo.manager.AdManager;
import com.windmill.android.demo.view.AdChannelSelector;

import java.util.Arrays;
import java.util.List;

public class NativeAdActivity extends Activity {

    private AdChannelSelector adChannelSelector;
    private String placementId;
    private String channel;

    private final List<String> list = Arrays.asList("9243878171116818", "3969166469641223");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        // 初始化广告渠道选择器
        adChannelSelector = findViewById(R.id.ad_channel_selector);
        adChannelSelector.setAdType(AdManager.AD_TYPE_NATIVE);
        adChannelSelector.setOnAdPlacementSelectedListener(new AdChannelSelector.OnAdPlacementSelectedListener() {
            @Override
            public void onAdPlacementSelected(String channel, String placementId, String placementName) {
                NativeAdActivity.this.channel = channel;
                NativeAdActivity.this.placementId = placementId;
                Log.d("lance", "Selected: channel=" + channel + ", placementId=" + placementId + ", placementName=" + placementName);
            }
        });

        bindButton(R.id.unified_native_ad_button, NativeAdUnifiedActivity.class);
        bindButton(R.id.unified_native_ad_list_button, NativeAdUnifiedListActivity.class);
        bindButton(R.id.unified_native_ad_recycle_button, NativeAdUnifiedRecycleActivity.class);
        bindButton(R.id.unified_native_ad_draw_button, NativeAdDrawActivity.class);
    }

    private void bindButton(@IdRes int id, final Class clz) {
        this.findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clz == NativeAdDrawActivity.class) {
                    if (!list.contains(placementId)) {
                        Toast.makeText(NativeAdActivity.this, "只支持draw类型广告", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Intent intent = new Intent(NativeAdActivity.this, clz);
                intent.putExtra("placementId", placementId);
                startActivity(intent);
            }
        });
    }
}