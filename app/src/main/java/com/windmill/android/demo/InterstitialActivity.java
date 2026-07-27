package com.windmill.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.windmill.android.demo.log.CallBackInfo;
import com.windmill.android.demo.log.CallBackItem;
import com.windmill.android.demo.log.ExpandAdapter;
import com.windmill.android.demo.manager.AdManager;
import com.windmill.android.demo.view.AdChannelSelector;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.interstitial.WMInterstitialAd;
import com.windmill.sdk.interstitial.WMInterstitialAdListener;
import com.windmill.sdk.interstitial.WMInterstitialAdRequest;
import com.windmill.sdk.models.AdInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InterstitialActivity extends Activity implements WMInterstitialAdListener{

    private WMInterstitialAd windInterstitialAd;
    private String placementId;
    private String userID = "123456789";

    private ListView listView;
    private ExpandAdapter adapter;
    private List<CallBackItem> callBackDataList = new ArrayList<>();

    private AdChannelSelector adChannelSelector;

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
        // 初始化广告渠道选择器
        adChannelSelector = findViewById(R.id.ad_channel_selector);
        adChannelSelector.setAdType(AdManager.AD_TYPE_INTERSTITIAL);
        adChannelSelector.setOnAdPlacementSelectedListener(new AdChannelSelector.OnAdPlacementSelectedListener() {
            @Override
            public void onAdPlacementSelected(String channel, String placementId, String placementName) {
                InterstitialActivity.this.placementId = placementId;
                Log.d("lance", "Selected: channel=" + channel + ", placementId=" + placementId + ", placementName=" + placementName);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        initCallBack();

    }

    public void ButtonClick(View view) {
        int id = view.getId();
        if(id == R.id.bt_load_ad){
            resetCallBackData();
            if(adapter != null){
                adapter.notifyDataSetChanged();
            }
            windInterstitialAd = new WMInterstitialAd(this, new WMInterstitialAdRequest(placementId, String.valueOf(0), null));
            windInterstitialAd.setInterstitialAdListener(this);
            windInterstitialAd.loadAd();
        }else if(id == R.id.bt_show_ad){
            HashMap option = new HashMap();
            option.put(WMConstants.AD_SCENE_ID, "567");
            option.put(WMConstants.AD_SCENE_DESC, "转盘抽奖");
            if (windInterstitialAd != null && windInterstitialAd.isReady()) {
                windInterstitialAd.showAd(this, option);
            } else {
                Log.d("lance", "------Ad is not Ready------");
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (windInterstitialAd != null) {
            windInterstitialAd.destroy();
            windInterstitialAd = null;
        }
    }

    @Override
    public void onInterstitialAdLoadSuccess(final String placementId) {
        Log.d("lance", "------onInterstitialAdLoadSuccess------" + placementId);
        logCallBack("onInterstitialAdLoadSuccess", "");
    }

    @Override
    public void onInterstitialAdPlayStart(AdInfo adInfo) {
        Log.d("lance", "------onInterstitialAdPlayStart------" + adInfo.getPlacementId());
        logCallBack("onInterstitialAdPlayStart", "");
    }

    @Override
    public void onInterstitialAdPlayEnd(AdInfo adInfo) {
        Log.d("lance", "------onInterstitialAdPlayEnd------" + adInfo.getPlacementId());
        logCallBack("onInterstitialAdPlayEnd", "");
    }

    @Override
    public void onInterstitialAdClicked(AdInfo adInfo) {
        Log.d("lance", "------onInterstitialAdClicked------" + adInfo.getPlacementId());
        logCallBack("onInterstitialAdClicked", "");
    }

    @Override
    public void onInterstitialAdClosed(AdInfo adInfo) {
        Log.d("lance", "------onInterstitialAdClosed------" + adInfo.getPlacementId());
        logCallBack("onInterstitialAdClosed", "");
    }

    @Override
    public void onInterstitialAdLoadError(final WindMillError error, final String placementId) {
        Log.d("lance", "------onInterstitialAdLoadError------" + windInterstitialAd.getLoadFailMessages() + ":" + placementId + " " + error);
        logCallBack("onInterstitialAdLoadError", windInterstitialAd.getLoadFailMessages() + ":" + placementId);
    }

    @Override
    public void onInterstitialAdPlayError(final WindMillError error, final String placementId) {
        Log.d("lance", "------onInterstitialAdPlayError------" + windInterstitialAd.getLoadFailMessages() + ":" + placementId + " " + error);
        logCallBack("onInterstitialAdPlayError", windInterstitialAd.getLoadFailMessages() + ":" + placementId);
    }

    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.INTERSTITIAL_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.INTERSTITIAL_CALLBACK[i], "", false, false));
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
}