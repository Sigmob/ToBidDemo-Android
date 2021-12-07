package com.windmill.android.demo;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.reward.WMRewardAd;
import com.windmill.sdk.reward.WMRewardAdListener;
import com.windmill.sdk.reward.WMRewardAdRequest;
import com.windmill.sdk.reward.WMRewardInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RewardVideoActivity extends AppCompatActivity {
    private Button loadAdBtn;
    private Button playAdBtn;
    private TextView logTextView;
    private WMRewardAd windRewardedVideoAd;
    private String placementId;
    private String userID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_video);

        loadAdBtn = this.findViewById(R.id.loadAd_button);
        playAdBtn = this.findViewById(R.id.playAd_button);

        logTextView = this.findViewById(R.id.logView);
        logTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        logTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        updatePlacement();

        Map<String, Object> options = new HashMap<>();
        options.put("lance", String.valueOf(userID));
        windRewardedVideoAd = new WMRewardAd(this, new WMRewardAdRequest(placementId, userID, options));
        windRewardedVideoAd.setWindRewardedVideoAdListener(new WMRewardAdListener() {
            @Override
            public void onVideoAdLoadSuccess(final String placementId) {
                RewardVideoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logMessage("onVideoAdLoadSuccess [ " + placementId + " ]");
                    }
                });
            }

            @Override
            public void onVideoAdPlayEnd(final String placementId) {
                RewardVideoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logMessage("onVideoAdPlayEnd [ " + placementId + " ]");
                    }
                });
            }

            @Override
            public void onVideoAdPlayStart(final String placementId) {
                RewardVideoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logMessage("onVideoAdPlayStart [ " + placementId + " ]");
                    }
                });
            }

            @Override
            public void onVideoAdClicked(final String placementId) {
                RewardVideoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logMessage("onVideoAdClicked [ " + placementId + " ]");
                    }
                });
            }

            @Override
            public void onVideoAdClosed(final String placementId) {
                RewardVideoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logMessage("onVideoAdClosed() [" + placementId + "]");
                    }
                });
            }

            @Override
            public void onVideoRewarded(final WMRewardInfo rewardInfo, final String placementId) {
                RewardVideoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logMessage("onVideoRewarded() called with: reward = [" + rewardInfo.toString() + "], placementId = [" + placementId + "]");
                    }
                });
            }

            @Override
            public void onVideoAdLoadError(final WindMillError error, final String placementId) {
                RewardVideoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logMessage("onVideoAdLoadError() called with: error = [" + error + "], placementId = [" + placementId + "]");
                    }
                });
            }

            @Override
            public void onVideoAdPlayError(final WindMillError error, final String placementId) {
                RewardVideoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logMessage("onVideoAdPlayError() called with: error = [" + error + "], placementId = [" + placementId + "]");
                    }
                });
            }
        });
    }

    public void buttonClick(View view) {
        switch (view.getId()) {
            case R.id.loadAd_button:
                if (windRewardedVideoAd != null) {
                    windRewardedVideoAd.loadAd();
                }
                break;
            case R.id.playAd_button:
                HashMap option = new HashMap();
                option.put(WMConstants.AD_SCENE_ID, "567");
                option.put(WMConstants.AD_SCENE_DESC, "转盘抽奖");
                if (windRewardedVideoAd != null && windRewardedVideoAd.isReady()) {
                    windRewardedVideoAd.show(this, option);
                } else {
                    logMessage("Ad is not Ready");
                }
                break;
            case R.id.cleanLog_button:
                cleanLog();
                break;
        }
    }

    private void updatePlacement() {

        SharedPreferences sharedPreferences = this.getSharedPreferences("setting", 0);

        placementId = sharedPreferences.getString(Constants.CONF_REWARD_PLACEMENTID, Constants.reward_placement_id);
        userID = sharedPreferences.getString(Constants.CONF_USERID, "");

        loadAdBtn.setText("load  " + placementId);
        playAdBtn.setText("play " + placementId);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void cleanLog() {
        logTextView.setText("");
    }

    private static SimpleDateFormat dateFormat = null;

    private static SimpleDateFormat getDateTimeFormat() {

        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss SSS", Locale.CHINA);
        }
        return dateFormat;
    }

    private void logMessage(String message) {
        Date date = new Date();
        logTextView.append(getDateTimeFormat().format(date) + " " + message + '\n');
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (windRewardedVideoAd != null) {
            windRewardedVideoAd.destroy();
            windRewardedVideoAd = null;
        }
    }
}