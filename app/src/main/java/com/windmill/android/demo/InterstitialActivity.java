package com.windmill.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.interstitial.WMInterstitialAd;
import com.windmill.sdk.interstitial.WMInterstitialAdListener;
import com.windmill.sdk.interstitial.WMInterstitialAdRequest;

import java.util.HashMap;
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

    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.bt_load_ad:
                updatePlacementId();
                if (windInterstitialAd == null) {
                    Map<String, Object> options = new HashMap<>();
                    options.put("user_id", String.valueOf(userID));
                    windInterstitialAd = new WMInterstitialAd(this, new WMInterstitialAdRequest(placementId, userID, options));
                    windInterstitialAd.setWindInterstitialAdListener(this);
                }
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
        if (selectedId == 0) {//SigMob不支持半屏
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
        if (selectedId == 0) {//SigMob不支持半屏
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
        Toast.makeText(InterstitialActivity.this, "onInterstitialAdLoadSuccess", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInterstitialAdPlayStart(final String placementId) {
        Log.d("lance", "------onInterstitialAdPlayStart------" + placementId);
    }

    @Override
    public void onInterstitialAdPlayEnd(final String placementId) {
        Log.d("lance", "------onInterstitialAdPlayEnd------" + placementId);
    }

    @Override
    public void onInterstitialAdClicked(final String placementId) {
        Log.d("lance", "------onInterstitialAdClicked------" + placementId);
    }

    @Override
    public void onInterstitialAdClosed(final String placementId) {
        Log.d("lance", "------onInterstitialAdClosed------" + placementId);
    }

    @Override
    public void onInterstitialAdLoadError(final WindMillError error, final String placementId) {
        Log.d("lance", "------onInterstitialAdLoadError------" + error.toString() + ":" + placementId);
    }

    @Override
    public void onInterstitialAdPlayError(final WindMillError error, final String placementId) {
        Log.d("lance", "------onInterstitialAdPlayError------" + error.toString() + ":" + placementId);
    }
}