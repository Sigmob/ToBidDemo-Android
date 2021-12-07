package com.windmill.android.demo.natives;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.windmill.android.demo.R;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.natives.WMNativeAd;
import com.windmill.sdk.natives.WMNativeAdContainer;
import com.windmill.sdk.natives.WMNativeAdData;
import com.windmill.sdk.natives.WMNativeAdDataType;
import com.windmill.sdk.natives.WMNativeAdRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdUnifiedActivity extends AppCompatActivity {

    private ViewGroup adContainer;
    private WMNativeAd windNativeAd;
    private int userID = 0;
    private String placementId;
    private List<WMNativeAdData> nativeAdDataList;

    private EditText editTextWidth, editTextHeight; // 编辑框输入的宽高
    private int adWidth, adHeight; // 广告宽高
    private CheckBox checkBoxFullWidth, checkBoxAutoHeight;

    private void getExtraInfo() {
        Intent intent = getIntent();
        placementId = intent.getStringExtra("placementId");
        if (TextUtils.isEmpty(placementId)) {
            String[] stringArray = getResources().getStringArray(R.array.native_id_value);
            placementId = stringArray[0];
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_unified);
        adContainer = findViewById(R.id.native_ad_container);
        getExtraInfo();

        editTextWidth = (EditText) findViewById(R.id.editWidth);
        editTextHeight = (EditText) findViewById(R.id.editHeight);

        checkBoxFullWidth = (CheckBox) findViewById(R.id.checkboxFullWidth);
        checkBoxAutoHeight = (CheckBox) findViewById(R.id.checkboxAutoHeight);
        checkBoxFullWidth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextWidth.setText("0");
                    editTextWidth.setEnabled(false);
                } else {
                    editTextWidth.setText("340");
                    editTextWidth.setEnabled(true);
                }
            }
        });
        checkBoxAutoHeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextHeight.setText("0");
                    editTextHeight.setEnabled(false);
                } else {
                    editTextHeight.setText("320");
                    editTextHeight.setEnabled(true);
                }
            }
        });
    }

    public void ButtonClick(View view) {
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

    private boolean checkEditTextEmpty() {
        String width = editTextWidth.getText().toString();
        String height = editTextHeight.getText().toString();
        if (TextUtils.isEmpty(width) || TextUtils.isEmpty(height)) {
            Toast.makeText(this, "请先输入广告位的宽、高！", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public static int screenWidthAsIntDips(Context context) {
        int pixels = context.getResources().getDisplayMetrics().widthPixels;
        float density = context.getResources().getDisplayMetrics().density;
        return (int) ((pixels / density) + 0.5f);
    }

    private void loadNativeAd() {
        Log.d("lance", "-----------loadNativeAd-----------");

        if (checkEditTextEmpty()) {
            return;
        }

        adWidth = Integer.valueOf(editTextWidth.getText().toString());
        adHeight = Integer.valueOf(editTextHeight.getText().toString());

        if (adWidth == 0) {//最大宽度
            adWidth = screenWidthAsIntDips(this) - 20;//减20因为容器有个margin 10dp//340
        }

        if (adHeight == 0) {
            adHeight = WMConstants.AUTO_SIZE;//自适应高度
        }

        userID++;
        Map<String, Object> options = new HashMap<>();
        options.put(WMConstants.AD_WIDTH, adWidth);//针对于模版广告有效、单位dp
        options.put(WMConstants.AD_HEIGHT, adHeight);//自适应高度
        options.put("user_id", String.valueOf(userID));
        if (windNativeAd == null) {
            windNativeAd = new WMNativeAd(this, new WMNativeAdRequest(placementId, String.valueOf(userID), 3, options));
        }

        windNativeAd.loadAd(new WMNativeAd.NativeAdLoadListener() {
            @Override
            public void onError(WindMillError error, String placementId) {
                Log.d("lance", "onError:" + error.toString() + ":" + placementId);
                Toast.makeText(NativeAdUnifiedActivity.this, "onError", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFeedAdLoad(String placementId) {
                Toast.makeText(NativeAdUnifiedActivity.this, "onFeedAdLoad", Toast.LENGTH_SHORT).show();
                List<WMNativeAdData> unifiedADData = windNativeAd.getNativeADDataList();
                if (unifiedADData != null && unifiedADData.size() > 0) {
                    Log.d("lance", "onFeedAdLoad:" + unifiedADData.size());
                    nativeAdDataList = unifiedADData;
                }
            }
        });
    }

    private void showNativeAd() {
        Log.d("lance", "-----------showNativeAd-----------");
        if (nativeAdDataList != null && nativeAdDataList.size() > 0) {
            WMNativeAdData nativeAdData = nativeAdDataList.get(0);

            //先绑定监听器
            bindListener(nativeAdData, placementId);

            if (nativeAdData.isExpressAd()) {//模版广告
                nativeAdData.render();//onRenderSuccess
//                View expressAdView = nativeAdData.getExpressAdView();
//                //媒体最终将要展示广告的容器
//                if (adContainer != null) {
//                    adContainer.removeAllViews();
//                    adContainer.addView(expressAdView);
//                }
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
                    Log.d("lance", "----------onDownloadActive----------");
                }

                @Override
                public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                    Log.d("lance", "----------onDownloadPaused----------");
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
        if (nativeAdDataList != null && nativeAdDataList.size() > 0) {
            for (WMNativeAdData ad : nativeAdDataList) {
                if (ad != null) {
                    ad.destroy();
                }
            }
        }
        if (windNativeAd != null) {
            windNativeAd.destroy();
        }
    }

}