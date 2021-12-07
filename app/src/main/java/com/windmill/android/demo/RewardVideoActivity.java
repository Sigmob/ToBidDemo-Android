package com.windmill.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.reward.WMRewardAd;
import com.windmill.sdk.reward.WMRewardAdListener;
import com.windmill.sdk.reward.WMRewardAdRequest;
import com.windmill.sdk.reward.WMRewardInfo;

import java.util.HashMap;
import java.util.Map;

public class RewardVideoActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;
    private WMRewardAd windRewardedVideoAd;
    private String placementId;
    private String userID = "123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_video);

        spinner = findViewById(R.id.id_spinner);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.reward_adapter));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        WebView.setWebContentsDebuggingEnabled(true);

        String[] stringArray = getResources().getStringArray(R.array.reward_adapter);
        placementId = stringArray[0];

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", String.valueOf(userID));
        windRewardedVideoAd = new WMRewardAd(this, new WMRewardAdRequest(placementId, userID, options));
        windRewardedVideoAd.setWindRewardedVideoAdListener(new WMRewardAdListener() {
            @Override
            public void onVideoAdLoadSuccess(final String placementId) {
                Log.d("lance", "------onVideoAdLoadSuccess------" + placementId);
                Toast.makeText(RewardVideoActivity.this, "onVideoAdLoadSuccess", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVideoAdPlayEnd(final String placementId) {
                Log.d("lance", "------onVideoAdPlayEnd------" + placementId);
            }

            @Override
            public void onVideoAdPlayStart(final String placementId) {
                Log.d("lance", "------onVideoAdPlayStart------" + placementId);
            }

            @Override
            public void onVideoAdClicked(final String placementId) {
                Log.d("lance", "------onVideoAdClicked------" + placementId);
            }

            @Override
            public void onVideoAdClosed(final String placementId) {
                Log.d("lance", "------onVideoAdClosed------" + placementId);
            }

            @Override
            public void onVideoRewarded(final WMRewardInfo rewardInfo, final String placementId) {
                Log.d("lance", "------onVideoRewarded------" + rewardInfo.toString() + ":" + placementId);
            }

            @Override
            public void onVideoAdLoadError(final WindMillError error, final String placementId) {
                Log.d("lance", "------onVideoAdLoadError------" + error.toString() + ":" + placementId);
            }

            @Override
            public void onVideoAdPlayError(final WindMillError error, final String placementId) {
                Log.d("lance", "------onVideoAdPlayError------" + error.toString() + ":" + placementId);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (windRewardedVideoAd != null) {
            windRewardedVideoAd.destroy();
            windRewardedVideoAd = null;
        }
    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.bt_load_ad:
                if (windRewardedVideoAd != null) {
                    windRewardedVideoAd.loadAd();
                }
                break;
            case R.id.bt_show_ad:
                HashMap option = new HashMap();
                option.put(WMConstants.AD_SCENE_ID, "567");
                option.put(WMConstants.AD_SCENE_DESC, "转盘抽奖");
                if (windRewardedVideoAd != null && windRewardedVideoAd.isReady()) {
                    windRewardedVideoAd.show(this, option);
                } else {
                    Log.d("lance", "------Ad is not Ready------");
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("lance", "------onItemSelected------" + position);
        String[] stringArray = getResources().getStringArray(R.array.reward_id_value);
        placementId = stringArray[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("lance", "------onNothingSelected------");
    }
}