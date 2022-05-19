package com.windmill.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.windmill.android.demo.log.CallBackInfo;
import com.windmill.android.demo.log.CallBackItem;
import com.windmill.android.demo.log.ExpandAdapter;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.banner.WMBannerAdListener;
import com.windmill.sdk.banner.WMBannerAdRequest;
import com.windmill.sdk.banner.WMBannerView;
import com.windmill.sdk.models.AdInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannerActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;
    private WMBannerView mBannerView;
    private String placementId;
    private String userID = "123456789";

    private ListView listView;
    private ExpandAdapter adapter;
    private List<CallBackItem> callBackDataList = new ArrayList<>();
    private ViewGroup adContainer;

    private void initCallBack() {
        resetCallBackData();
        listView = findViewById(R.id.callback_lv);
        adapter = new ExpandAdapter(this, callBackDataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        adContainer = findViewById(R.id.banner_ad_container);
        spinner = findViewById(R.id.id_spinner);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.banner_adapter));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        WebView.setWebContentsDebuggingEnabled(true);

        String[] stringArray = getResources().getStringArray(R.array.banner_id_value);
        placementId = stringArray[0];

        initCallBack();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBannerView != null) {
            mBannerView.destroy();
            mBannerView = null;
        }
    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.bt_load_ad:
                resetCallBackData();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                loadAd();
                break;
            case R.id.bt_show_ad:
                /**
                 * 广告是否有效
                 */
                boolean ready = mBannerView.isReady();
                Log.d("lance", "------Ad is Ready------" + ready);
                if (mBannerView != null) {
                    //媒体最终将要展示广告的容器
                    if (adContainer != null) {
                        adContainer.removeAllViews();
                        adContainer.addView(mBannerView);
                    }
                }
                break;
        }
    }

    private void loadAd() {
        Map<String, Object> options = new HashMap<>();
        options.put("user_id", String.valueOf(userID));
        mBannerView = new WMBannerView(this);
        mBannerView.setAdListener(new WMBannerAdListener() {
            @Override
            public void onAdLoadSuccess(String placementId) {
                Log.d("lance", "------onAdLoadSuccess------" + placementId);
                logCallBack("onAdLoadSuccess", "");
            }

            @Override
            public void onAdLoadError(WindMillError error, String placementId) {
                Log.d("lance", "------onAdLoadError------" + error.toString() + ":" + placementId);
                logCallBack("onAdLoadError", error.toString());
            }

            @Override
            public void onAdShown(AdInfo adInfo) {
                Log.d("lance", "------onAdShown------" + adInfo.getPlacementId());
                logCallBack("onAdShown", "");
            }

            @Override
            public void onAdClicked(AdInfo adInfo) {
                Log.d("lance", "------onAdClicked------" + adInfo.getPlacementId());
                logCallBack("onAdClicked", "");
            }

            @Override
            public void onAdClosed(AdInfo adInfo) {
                Log.d("lance", "------onAdClosed------" + adInfo.getPlacementId());
                logCallBack("onAdClosed", "");

                if (adContainer != null) {
                    adContainer.removeAllViews();
                }
            }

            @Override
            public void onAdAutoRefreshed(AdInfo adInfo) {
                Log.d("lance", "------onAdAutoRefreshed------" + adInfo.getPlacementId());
                logCallBack("onAdAutoRefreshed", "");
            }

            @Override
            public void onAdAutoRefreshFail(WindMillError error, String placementId) {
                Log.d("lance", "------onAdAutoRefreshFail------" + error.toString() + ":" + placementId);
                logCallBack("onAdAutoRefreshFail", error.toString());
            }
        });

        mBannerView.setAutoAnimation(true);
        mBannerView.loadAd(new WMBannerAdRequest(placementId, userID, options));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] stringArray = getResources().getStringArray(R.array.banner_id_value);
        placementId = stringArray[position];
        Log.d("lance", "------onItemSelected------" + position + ":" + placementId);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("lance", "------onNothingSelected------");
    }

    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.BANNER_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.BANNER_CALLBACK[i], "", false, false));
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