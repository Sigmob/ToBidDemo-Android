package com.windmill.android.demo.natives;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.windmill.android.demo.Constants;
import com.windmill.android.demo.R;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.natives.WMNativeAdContainer;
import com.windmill.sdk.natives.WMNativeAdData;
import com.windmill.sdk.natives.WMNativeAdDataType;
import com.windmill.sdk.natives.WMNativeAdRequest;
import com.windmill.sdk.natives.WMNativeUnifiedAd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdUnifiedActivity extends AppCompatActivity {

    private ViewGroup adContainer;
    private Button loadAdBtn;
    private Button playAdBtn;
    private WMNativeUnifiedAd windNativeUnifiedAd;
    private int userID = 0;
    private String placementId;
    private List<WMNativeAdData> unifiedADDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_unified);
        adContainer = findViewById(R.id.native_ad_container);
        loadAdBtn = this.findViewById(R.id.load_native_button);
        playAdBtn = this.findViewById(R.id.show_native_button);
        updatePlacement();
    }

    public void buttonClick(View view) {
        switch (view.getId()) {
            case R.id.load_native_button:
                //加载原生广告
                loadNativeAd();
                break;
            case R.id.show_native_button:
                //展示原生广告
                showNativeAd();
                break;
        }
    }

    private void loadNativeAd() {
        Log.d("lance", "-----------loadNativeAd-----------");
        userID++;
        Map<String, Object> options = new HashMap<>();
        options.put("user_id", String.valueOf(userID));
        if (windNativeUnifiedAd == null) {
            windNativeUnifiedAd = new WMNativeUnifiedAd(this, new WMNativeAdRequest(placementId, String.valueOf(userID), 3, options));
        }

        windNativeUnifiedAd.loadAd(new WMNativeUnifiedAd.NativeAdLoadListener() {
            @Override
            public void onError(WindMillError error, String placementId) {
                Log.d("lance", "onError:" + error.toString() + ":" + placementId);
                Toast.makeText(NativeAdUnifiedActivity.this, "onError", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFeedAdLoad(String placementId) {
                Toast.makeText(NativeAdUnifiedActivity.this, "onFeedAdLoad", Toast.LENGTH_SHORT).show();
                List<WMNativeAdData> unifiedADData = windNativeUnifiedAd.getNativeADDataList();
                if (unifiedADData != null && unifiedADData.size() > 0) {
                    Log.d("lance", "onFeedAdLoad:" + unifiedADData.size());
                    unifiedADDataList = unifiedADData;
                }
            }
        });
    }

    private void showNativeAd() {
        Log.d("lance", "-----------showNativeAd-----------");
        if (unifiedADDataList != null && unifiedADDataList.size() > 0) {
            WMNativeAdData nativeAdData = unifiedADDataList.get(0);

            //先绑定监听器
            bindListener(nativeAdData, placementId);

            if (nativeAdData.isExpressAd()) {//模版广告
                nativeAdData.render();
//                View expressAdView = nativeAdData.getExpressAdView();

            } else {//自渲染广告
                //创建一个装整个自渲染广告的容器
                WMNativeAdContainer windContainer = new WMNativeAdContainer(this);
                //媒体自渲染的View
                NativeAdDemoRender adRender = new NativeAdDemoRender();
                //将容器和view链接起来
                nativeAdData.connectAdToView(this, windContainer, adRender);

                //媒体最终将要展示广告的容器
                if (adContainer != null) {
                    adContainer.removeAllViews();
                    adContainer.addView(windContainer);
                }
            }
        }
    }

    private void bindListener(WMNativeAdData nativeAdData, String id) {
        //设置广告交互监听
        nativeAdData.setInteractionListener(new WMNativeAdData.NativeAdInteractionListener() {
            @Override
            public void onADExposed() {
                Log.d("lance", "----------onADExposed----------");
            }

            @Override
            public void onADClicked() {
                Log.d("lance", "----------onADClicked----------");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.d("lance", "----------onRenderFail----------:" + msg + ":" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.d("lance", "----------onRenderSuccess----------:" + width + ":" + height);
                //媒体最终将要展示广告的容器
                if (adContainer != null) {
                    adContainer.removeAllViews();
                    adContainer.addView(view);
                }
            }

            @Override
            public void onADError(WindMillError error) {
                Log.d("lance", "----------onADError----------:" + error.toString());
            }

            @Override
            public void onADStatusChanged(String ctaText) {
                Log.d("lance", "----------onADStatusChanged----------:" + ctaText);
//                    updateAdAction(ctaText);
            }
        });

        //设置media监听
        if (nativeAdData.getAdPatternType() == WMNativeAdDataType.NATIVE_VIDEO_AD) {
            nativeAdData.setMediaListener(new WMNativeAdData.NativeADMediaListener() {
                @Override
                public void onVideoLoad() {
                    Log.d("lance", "----------onVideoLoad----------");
                }

                @Override
                public void onVideoError(WindMillError error) {
                    Log.d("lance", "----------onVideoError----------:" + error.toString());
                }

                @Override
                public void onVideoStart() {
                    Log.d("lance", "----------onVideoStart----------");
                }

                @Override
                public void onVideoPause() {
                    Log.d("lance", "----------onVideoPause----------");
                }

                @Override
                public void onVideoResume() {
                    Log.d("lance", "----------onVideoResume----------");
                }

                @Override
                public void onVideoCompleted() {
                    Log.d("lance", "----------onVideoCompleted----------");
                }
            });
        }

        if (nativeAdData.getInteractionType() == WMConstants.INTERACTION_TYPE_DOWNLOAD) {
            nativeAdData.setDownloadListener(new WMNativeAdData.AppDownloadListener() {
                @Override
                public void onIdle() {
                    Log.d("lance", "----------onIdle----------");
                }

                @Override
                public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                    Log.d("lance", "----------onADExposed----------");
                }

                @Override
                public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                    Log.d("lance", "----------onDownloadActive----------");
                }

                @Override
                public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                    Log.d("lance", "----------onDownloadFailed----------");
                }

                @Override
                public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                    Log.d("lance", "----------onDownloadFinished----------");
                }

                @Override
                public void onInstalled(String fileName, String appName) {
                    Log.d("lance", "----------onInstalled----------");
                }
            });
        }

        //设置dislike弹窗
        nativeAdData.setDislikeInteractionCallback(this, new WMNativeAdData.DislikeInteractionCallback() {
            @Override
            public void onShow() {
                Log.d("lance", "----------onShow----------");
            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                Log.d("lance", "----------onSelected----------:" + position + ":" + value + ":" + enforce);
                if (adContainer != null) {
                    adContainer.removeAllViews();
                }
            }

            @Override
            public void onCancel() {
                Log.d("lance", "----------onCancel----------");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unifiedADDataList != null && unifiedADDataList.size() > 0) {
            for (WMNativeAdData ad : unifiedADDataList) {
                if (ad != null) {
                    ad.destroy();
                }
            }
        }
        if (windNativeUnifiedAd != null) {
            windNativeUnifiedAd.destroy();
        }
    }

    private void updatePlacement() {

        SharedPreferences sharedPreferences = this.getSharedPreferences("setting", 0);

        placementId = sharedPreferences.getString(Constants.CONF_UNIFIED_NATIVE_PLACEMENTID, Constants.native_unified_placement_id);

        loadAdBtn.setText("加载自渲染广告:" + placementId);
        playAdBtn.setText("展示自渲染广告: " + placementId);
        Toast.makeText(this, "updatePlacement", Toast.LENGTH_SHORT).show();
    }
}