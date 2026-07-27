package com.windmill.android.demo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.windmill.android.demo.log.CallBackInfo;
import com.windmill.android.demo.log.CallBackItem;
import com.windmill.android.demo.log.ExpandAdapter;
import com.windmill.android.demo.manager.AdManager;
import com.windmill.android.demo.view.AdChannelSelector;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.models.AdInfo;
import com.windmill.sdk.splash.WMSplashAd;
import com.windmill.sdk.splash.WMSplashAdListener;
import com.windmill.sdk.splash.WMSplashAdRequest;

import java.util.ArrayList;
import java.util.List;

public class SplashAdActivity extends Activity {
    private String placementId;
    private ListView listView;
    private ExpandAdapter adapter;
    private ViewGroup splashLY;
    private WMSplashAd splashAd;

    private List<CallBackItem> callBackDataList = new ArrayList<>();

    private AdChannelSelector adChannelSelector;

    private void initViewGroup(Activity activity) {
        splashLY = new RelativeLayout(activity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        viewGroup.addView(splashLY, layoutParams);
    }

    private void initCallBack() {
        resetCallBackData();
        listView = findViewById(R.id.callback_lv);
        adapter = new ExpandAdapter(this, callBackDataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("lance", "------onItemClick------" + position);
            CallBackItem callItem = callBackDataList.get(position);
            if (callItem != null) {
                if (callItem.is_expand()) {
                    callItem.set_expand(false);
                } else {
                    callItem.set_expand(true);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ad);

        // 初始化广告渠道选择器
        adChannelSelector = findViewById(R.id.ad_channel_selector);
        adChannelSelector.setAdType(AdManager.AD_TYPE_SPLASH);
        adChannelSelector.setOnAdPlacementSelectedListener(new AdChannelSelector.OnAdPlacementSelectedListener() {
            @Override
            public void onAdPlacementSelected(String channel, String placementId, String placementName) {
                SplashAdActivity.this.placementId = placementId;
                Log.d("lance", "Selected: channel=" + channel + ", placementId=" + placementId + ", placementName=" + placementName);
            }
        });

        CheckBox fullScreen = findViewById(R.id.cb_fullscreen);
        CheckBox selfLogo = findViewById(R.id.cb_self_logo);
        fullScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = SplashAdActivity.this.getSharedPreferences("setting", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.CONF_FULL_SCREEN, isChecked);
                editor.apply();
            }
        });

        selfLogo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = SplashAdActivity.this.getSharedPreferences("setting", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.CONF_SELF_LOGO, isChecked);
                editor.apply();
            }
        });

        initCallBack();
    }

    public void ButtonClick(View view) {
        int id = view.getId();
        if (id == R.id.bt_load) {
            resetCallBackData();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            loadSplashAd();

        } else if (id == R.id.bt_show) {
            showSplashAd();
        }
    }

    private void loadSplashAd() {

        initViewGroup(this);

        WMSplashAdRequest adRequest = new WMSplashAdRequest(placementId, String.valueOf(0), null);
        splashAd = new WMSplashAd(this, adRequest);
        splashAd.setSplashAdListener(new WMSplashAdListener() {
            @Override
            public void onSplashAdFailToPresent(WindMillError windMillError, String s) {
                Log.d("lance", "------onSplashAdFailToPresent------" + splashAd.getLoadFailMessages() + ":" + placementId);
                logCallBack("onSplashAdFailToPresent", windMillError + ":" + placementId);
            }

            @Override
            public void onSplashAdSuccessPresent(AdInfo adInfo) {
                Log.d("lance", "------onSplashAdSuccessPresent------" + adInfo.getPlacementId());
                logCallBack("onSplashAdSuccessPresent", adInfo.getPlacementId());
            }

            @Override
            public void onSplashAdSuccessLoad(String placementId) {
                Log.d("lance", "------onSplashAdSuccessLoad------" + splashAd.isReady() + ":" + placementId);
                logCallBack("onSplashAdSuccessLoad", placementId);
            }

            @Override
            public void onSplashAdFailToLoad(WindMillError windMillError, String placementId) {
                Log.d("lance", "------onSplashAdFailToLoad------" + splashAd.getLoadFailMessages() + ":" + placementId + ":" + windMillError);
                logCallBack("onSplashAdFailToLoad", splashAd.getLoadFailMessages() + ":" + placementId);
            }

            @Override
            public void onSplashAdClicked(AdInfo adInfo) {
                Log.d("lance", "------onSplashAdClicked------" + adInfo.getPlacementId());
                logCallBack("onSplashAdClicked", adInfo.getPlacementId());
            }

            @Override
            public void onSplashClosed(AdInfo adInfo) {
                Log.d("lance", "------onSplashClosed------" + adInfo.getPlacementId());
                logCallBack("onSplashClosed", adInfo.getPlacementId());
                if (splashLY != null) {
                    splashLY.removeAllViews();
                    splashLY.setVisibility(View.GONE);
                }
            }
        });

        splashAd.loadAdOnly();
    }

    private void showSplashAd() {
        if (splashAd != null && splashAd.isReady()) {
//            splashAd.showAd(null);
            splashAd.showAd(splashLY);
        } else {
            Toast.makeText(SplashAdActivity.this, "Ad is not Ready", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.SPLASH_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.SPLASH_CALLBACK[i], "", false, false));
        }
    }

    private void logCallBack(String call, String child) {
        for (int i = 0; i < callBackDataList.size(); i++) {
            CallBackItem callItem = callBackDataList.get(i);
            if (callItem.getText().equals(call)) {
                callItem.set_callback(true);
                if (!TextUtils.isEmpty(child)) {
                    callItem.setChild_text(child);
                }
                break;
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (splashAd != null) {
            splashAd.destroy();
            splashAd = null;
        }
    }
}