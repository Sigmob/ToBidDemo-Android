package com.windmill.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.windmill.android.demo.log.CallBackInfo;
import com.windmill.android.demo.log.CallBackItem;
import com.windmill.android.demo.log.ExpandAdapter;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.interstitial.WMInterstitialAd;
import com.windmill.sdk.interstitial.WMInterstitialAdListener;
import com.windmill.sdk.interstitial.WMInterstitialAdRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterstitialActivity extends Activity implements WMInterstitialAdListener, AdapterView.OnItemSelectedListener {

    private WMInterstitialAd windInterstitialAd;
    private String placementId;
    private String userID = "123456789";
    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;

    private CheckBox halfScreen;
    private boolean isHalfScreen = false;
    private int selectedId = 0;

    private ListView listView;
    private ExpandAdapter adapter;
    private List<CallBackItem> callBackDataList = new ArrayList<>();

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
        setContentView(R.layout.activity_interstitial);

        spinner = findViewById(R.id.id_spinner);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.interstitial_adapter));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        halfScreen = findViewById(R.id.cb_fullscreen);
        halfScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isHalfScreen = isChecked;
            }
        });

        WebView.setWebContentsDebuggingEnabled(true);

        initCallBack();

    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.bt_load_ad:
                resetCallBackData();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                updatePlacementId();
                Map<String, Object> options = new HashMap<>();
                options.put("user_id", String.valueOf(userID));
                windInterstitialAd = new WMInterstitialAd(this, new WMInterstitialAdRequest(placementId, userID, options));
                windInterstitialAd.setInterstitialAdListener(this);
                windInterstitialAd.loadAd();
                break;
            case R.id.bt_show_ad:
                HashMap option = new HashMap();
                option.put(WMConstants.AD_SCENE_ID, "567");
                option.put(WMConstants.AD_SCENE_DESC, "转盘抽奖");
                if (windInterstitialAd != null && windInterstitialAd.isReady()) {
                    windInterstitialAd.show(this, option);
                } else {
                    Log.d("lance", "------Ad is not Ready------");
                }
                break;
        }
    }

    private void updatePlacementId() {
        if (selectedId == 0 || selectedId == 5) {//SigMob、Unity不支持半屏
            String[] stringArray = getResources().getStringArray(R.array.interstitial_full_id_value);
            placementId = stringArray[0];
        } else {
            String[] stringArray;
            if (isHalfScreen) {
                stringArray = getResources().getStringArray(R.array.interstitial_half_id_value);
            } else {
                stringArray = getResources().getStringArray(R.array.interstitial_full_id_value);
            }
            placementId = stringArray[selectedId];
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("lance", "------onItemSelected------" + position);
        selectedId = position;
        if (selectedId == 0 || selectedId == 5) {//SigMob、Unity不支持半屏
            halfScreen.setVisibility(View.GONE);
        } else {
            halfScreen.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("lance", "------onNothingSelected------");
    }

    @Override
    public void onInterstitialAdLoadSuccess(final String placementId) {
        Log.d("lance", "------onInterstitialAdLoadSuccess------" + placementId);
        logCallBack("onInterstitialAdLoadSuccess", "");
    }

    @Override
    public void onInterstitialAdPlayStart(final String placementId) {
        Log.d("lance", "------onInterstitialAdPlayStart------" + placementId);
        logCallBack("onInterstitialAdPlayStart", "");
    }

    @Override
    public void onInterstitialAdPlayEnd(final String placementId) {
        Log.d("lance", "------onInterstitialAdPlayEnd------" + placementId);
        logCallBack("onInterstitialAdPlayEnd", "");
    }

    @Override
    public void onInterstitialAdClicked(final String placementId) {
        Log.d("lance", "------onInterstitialAdClicked------" + placementId);
        logCallBack("onInterstitialAdClicked", "");
    }

    @Override
    public void onInterstitialAdClosed(final String placementId) {
        Log.d("lance", "------onInterstitialAdClosed------" + placementId);
        logCallBack("onInterstitialAdClosed", "");
    }

    @Override
    public void onInterstitialAdLoadError(final WindMillError error, final String placementId) {
        Log.d("lance", "------onInterstitialAdLoadError------" + error.toString() + ":" + placementId);
        logCallBack("onInterstitialAdLoadError", error.toString());
    }

    @Override
    public void onInterstitialAdPlayError(final WindMillError error, final String placementId) {
        Log.d("lance", "------onInterstitialAdPlayError------" + error.toString() + ":" + placementId);
        logCallBack("onInterstitialAdPlayError", error.toString());
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