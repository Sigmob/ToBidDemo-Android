package com.windmill.android.demo.natives;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.windmill.android.demo.R;
import com.windmill.android.demo.log.CallBackInfo;
import com.windmill.android.demo.log.CallBackItem;
import com.windmill.android.demo.log.ExpandAdapter;
import com.windmill.android.demo.log.MyListView;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.base.WMAutoAdLoadListener;
import com.windmill.sdk.models.AdInfo;
import com.windmill.sdk.natives.WMNativeAd;
import com.windmill.sdk.natives.WMNativeAdData;
import com.windmill.sdk.natives.WMNativeAdDataType;
import com.windmill.sdk.natives.WMNativeAdLoadListener;
import com.windmill.sdk.natives.WMNativeAdRequest;

import java.util.ArrayList;
import java.util.List;

public class NativeAdUnifiedActivity extends Activity {

    private ViewGroup adContainer;
    private WMNativeAd windNativeAd;
    private int userID = 0;
    private String placementId;
    private List<WMNativeAdData> nativeAdDataList;

    private EditText editTextWidth, editTextHeight; // 编辑框输入的宽高
    private int adWidth, adHeight; // 广告宽高
    private CheckBox checkBoxFullWidth, checkBoxAutoHeight;

    private MyListView listView;
    private ExpandAdapter adapter;
    private List<CallBackItem> callBackDataList = new ArrayList<>();

    private static final int DEFAULT_LOAD_COUNT = 1;

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

    private void getExtraInfo() {
        Intent intent = getIntent();
        placementId = intent.getStringExtra("placementId");
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

        initCallBack();
    }

    public void ButtonClick(View view) {
        int id = view.getId();
        if (id == R.id.load_native_button) {
            resetCallBackData();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            //加载原生广告
            loadNativeAd();
        } else if (id == R.id.show_native_button) {
            //展示原生广告
            showNativeAd();
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
        if (windNativeAd == null) {
            windNativeAd = new WMNativeAd(this, new WMNativeAdRequest(placementId, String.valueOf(userID), null));
        }

        windNativeAd.setAutoLoadListener(new WMAutoAdLoadListener() {
            @Override
            public void onAutoAdLoadSuccess(String placementId) {
                logCallBack("onAutoAdLoadSuccess", "" );
            }

            @Override
            public void onAutoAdLoadFail(WindMillError error, String placementId) {
                logCallBack("onAutoAdLoadFail", windNativeAd.getLoadFailMessages() + ":" + placementId);
            }
        });

        windNativeAd.setNativeAdLoadListener(new WMNativeAdLoadListener() {
            @Override
            public void onError(WindMillError windMillError, String s) {
                Log.d("lance", "onError:" + windNativeAd.getLoadFailMessages() + ":" + placementId + " " + windMillError);
                logCallBack("onError", windNativeAd.getLoadFailMessages() + ":" + placementId);
                Toast.makeText(NativeAdUnifiedActivity.this, "onError", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFeedAdLoad(List<WMNativeAdData> list, String s) {
                Toast.makeText(NativeAdUnifiedActivity.this, "onFeedAdLoad", Toast.LENGTH_SHORT).show();
                logCallBack("onFeedAdLoad", "");
                if (list != null && list.size() > 0) {
                    Log.d("lance", "onFeedAdLoad:" + list.size());
                    nativeAdDataList = list;
                }
            }
        });
        windNativeAd.loadAd(DEFAULT_LOAD_COUNT);

    }

    private void showNativeAd() {
        Log.d("lance", "-----------showNativeAd-----------");
        if (nativeAdDataList != null && nativeAdDataList.size() > 0) {
            WMNativeAdData nativeAdData = nativeAdDataList.get(0);
            prepareNativeData(nativeAdData);
            if (nativeAdData.isExpressAd()) {//模版广告
                nativeAdData.render();//onRenderSuccess
                View expressAdView = nativeAdData.getExpressAdView();
                //媒体最终将要展示广告的容器
                if (adContainer != null) {
                    if (expressAdView != null) {
                        adContainer.removeAllViews();
                        adContainer.addView(expressAdView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                }
            } else {//自渲染广告
                NativeAdDemoRender nativeAdDemoRender = new NativeAdDemoRender();
                View adView = nativeAdDemoRender.getAdView(this, nativeAdData);
                if (adContainer != null) {
                    if (adView.getParent() != null) {
                        ViewGroup root = (ViewGroup) adView.getParent();
                        root.removeView(adView);
                    }
                    adContainer.addView(adView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }
        }
    }

    private void prepareNativeData(WMNativeAdData nativeAdData) {
        if (nativeAdData == null) {
            return;
        }
        nativeAdData.setInteractionListener(new WMNativeAdData.NativeAdInteractionListener() {
            @Override
            public void onADExposed(AdInfo adInfo) {
                Log.d("lance", "onADExposed: " + adInfo);
                logCallBack("onADExposed", "");
            }

            @Override
            public void onADClicked(AdInfo adInfo) {
                Log.d("lance", "onADClicked: " + adInfo);
                logCallBack("onADClicked", "");
            }

            @Override
            public void onADRenderSuccess(AdInfo adInfo, View view, float width, float height) {
                runOnUiThread(() -> {
                    logCallBack("onADRenderSuccess", "");
                    Log.d("lance", "onADRenderSuccess: " + adInfo + "， width : " + width + "， height : " + height);
                    if (adContainer != null) {
                        adContainer.removeAllViews();
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT);
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                        adContainer.addView(view, layoutParams);
                    }
                });
            }

            @Override
            public void onADError(AdInfo adInfo, WindMillError error) {
                Log.d("lance", "onADError: " + adInfo + "， error : " + error);
                logCallBack("onADError", error.getMessage());
            }
        });

        if (nativeAdData.getAdPatternType() == WMNativeAdDataType.NATIVE_VIDEO_AD) {
            nativeAdData.setMediaListener(new WMNativeAdData.NativeADMediaListenerExt() {
                @Override
                public void onVideoProgressUpdate(long l, long l1) {
                    Log.d("lance", "视频播放进度更新: " + l + " / " + l1);
                }

                @Override
                public void onVideoLoad() {
                    Log.d("lance", "视频素材加载完成");
                }

                @Override
                public void onVideoError(WindMillError error) {
                    Log.d("lance", "视频播放出错: " + error);
                }

                @Override
                public void onVideoStart() {
                    Log.d("lance", "视频开始播放，时长：" + nativeAdData.getVideoDuration());
                }

                @Override
                public void onVideoPause() {
                    Log.d("lance", "视频暂停");
                }

                @Override
                public void onVideoResume() {
                    Log.d("lance", "视频继续");
                }

                @Override
                public void onVideoCompleted() {
                    Log.d("lance", "视频播放完成");
                }

            });
        }

        nativeAdData.setDownloadListener(new WMNativeAdData.AppDownloadListener() {
            @Override
            public void onIdle() {
                Log.d("lance", "下载状态: idle");
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                Log.d("lance", "下载中 " + currBytes + "/" + totalBytes);
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                Log.d("lance", "下载暂停");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                Log.d("lance", "下载失败");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                Log.d("lance", "下载完成");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                Log.d("lance", "安装完成");
            }
        });

        nativeAdData.setDislikeInteractionCallback(this, new WMNativeAdData.WMDislikeInteractionCallback() {
            @Override
            public void onShow() {
                Log.d("lance", "不喜欢展示");

            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                if (adContainer != null) {
                    adContainer.removeAllViews();
                }
                Log.d("lance", "不喜欢原因: " + value);
            }

            @Override
            public void onCancel() {
                Log.d("lance", "取消不喜欢操作");
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (nativeAdDataList != null && !nativeAdDataList.isEmpty()) {
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

    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.NATIVE_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.NATIVE_CALLBACK[i], "", false, false));
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