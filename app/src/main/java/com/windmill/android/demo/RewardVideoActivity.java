package com.windmill.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.windmill.android.demo.log.CallBackInfo;
import com.windmill.android.demo.log.CallBackItem;
import com.windmill.android.demo.log.ExpandAdapter;
import com.windmill.android.demo.manager.AdManager;
import com.windmill.android.demo.view.AdChannelSelector;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.models.AdInfo;
import com.windmill.sdk.reward.WMRewardAd;
import com.windmill.sdk.reward.WMRewardAdListener;
import com.windmill.sdk.reward.WMRewardAdRequest;
import com.windmill.sdk.reward.WMRewardInfo;

import java.util.ArrayList;
import java.util.List;

public class RewardVideoActivity extends Activity {

    private WMRewardAd windRewardedVideoAd;
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

        // 状态栏颜色设置 - 兼容API 33的版本
//        setupStatusBarColor();
        setContentView(R.layout.activity_reward_video);

        // 初始化广告渠道选择器
        adChannelSelector = findViewById(R.id.ad_channel_selector);
        adChannelSelector.setAdType(AdManager.AD_TYPE_REWARD);
        adChannelSelector.setOnAdPlacementSelectedListener(new AdChannelSelector.OnAdPlacementSelectedListener() {
            @Override
            public void onAdPlacementSelected(String channel, String placementId, String placementName) {
                RewardVideoActivity.this.placementId = placementId;
                Log.d("lance", "Selected: channel=" + channel + ", placementId=" + placementId + ", placementName=" + placementName);
            }
        });

        initCallBack();
    }

//    /**
//     * 设置状态栏颜色 - 兼容Android 13 (API 33)
//     */
//    private void setupStatusBarColor() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//
//            // 对于API 33+，需要确保窗口布局参数正确设置
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                // Android 13+ 特定设置
//                window.setDecorFitsSystemWindows(false);
//            }
//
//            // 移除可能冲突的标志
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//
//            // 添加必要的标志
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//            // 直接设置状态栏颜色
//            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
//
//            // 确保内容不会被状态栏遮挡
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
//                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            }
//        }
//    }

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
        int id = view.getId();
        if (id == R.id.bt_load_ad) {
            resetCallBackData();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            loadAd();
        } else if (id == R.id.bt_show_ad) {
            if (windRewardedVideoAd != null && windRewardedVideoAd.isReady()) {
                windRewardedVideoAd.showAd(this, null);
            } else {
                Log.d("lance", "------Ad is not Ready------");
            }
        }
    }

    private void loadAd() {
        windRewardedVideoAd = new WMRewardAd(this, new WMRewardAdRequest(placementId, String.valueOf(0), null));

        windRewardedVideoAd.setRewardAdListener(new WMRewardAdListener() {
            @Override
            public void onVideoAdLoadSuccess(final String placementId) {
                Log.d("lance", "------onVideoAdLoadSuccess------" + placementId);
                logCallBack("onVideoAdLoadSuccess", "");
            }

            @Override
            public void onVideoAdPlayEnd(AdInfo adInfo) {
                Log.d("lance", "------onVideoAdPlayEnd------" + adInfo.getPlacementId());
                logCallBack("onVideoAdPlayEnd", "");
            }

            @Override
            public void onVideoAdPlayStart(AdInfo adInfo) {
                Log.d("lance", "------onVideoAdPlayStart------" + adInfo.getPlacementId());
                logCallBack("onVideoAdPlayStart", "");
            }

            @Override
            public void onVideoAdClicked(AdInfo adInfo) {
                Log.d("lance", "------onVideoAdClicked------" + adInfo.getPlacementId());
                logCallBack("onVideoAdClicked", "");
            }

            @Override
            public void onVideoAdClosed(AdInfo adInfo) {
                Log.d("lance", "------onVideoAdClosed------" + adInfo.getPlacementId());
                logCallBack("onVideoAdClosed", "");
            }

            @Override
            public void onVideoRewarded(AdInfo adInfo, final WMRewardInfo rewardInfo) {
                Log.d("lance", "------onVideoRewarded------" + rewardInfo + ":" + adInfo.getPlacementId());
                logCallBack("onVideoRewarded", String.valueOf(rewardInfo));
            }

            @Override
            public void onVideoAdLoadError(final WindMillError error, final String placementId) {
                Log.d("lance", "onVideoAdLoadError------onVideoAdLoadError------:" + windRewardedVideoAd.getLoadFailMessages() + ":" + placementId + ":" + error);
                logCallBack("onVideoAdLoadError", windRewardedVideoAd.getLoadFailMessages() + ":" + placementId);
            }

            @Override
            public void onVideoAdPlayError(final WindMillError error, final String placementId) {
                Log.d("lance", "------onVideoAdPlayError------" + windRewardedVideoAd.getLoadFailMessages() + ":" + placementId + ":" + error);
                logCallBack("onVideoAdPlayError", windRewardedVideoAd.getLoadFailMessages() + ":" + placementId);
            }
        });

//        //自定义分组 https://doc.sigmob.com/tobid/21167/#2215510342
//        // 新增同一个placementID 下多个实例对象并发请求的场景，通过下面的接口在load之前传入自定义的参数可以做到进入不同的分组
//        //注意：！此接口的优先级高于initCustomMap、initPlacementCustomMap 两个接口，如果同时调用此接口中的key和Value值会覆盖前两个
//        HashMap<String, String> customGroup = new HashMap<>();
//        // 自定义参数的Key和Value
//        customGroup.put("sense", "load");
//        //所有广告对象有效,下面为激励广告对象的示例
//        windRewardedVideoAd.setCustomGroup(customGroup);


        //过滤器设置 https://doc.sigmob.com/tobid/21166/#637867375
//        WMAdFilter wmFilter = new WMAdFilter();//针对这个广告对象进行的过滤
//        wmFilter.equalTo(WMAdFilter.KEY_CHANNEL_ID, "16")//gdt渠道id
//                .equalTo(WMAdFilter.KEY_ADN_PLACEMENT_ID, "123")//渠道的广告位id
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMAdFilter.KEY_CHANNEL_ID, "13")//csj渠道id
//                .and()//与的关系：不开启新的过滤表达式:可写可不写
//                .equalTo(WMAdFilter.KEY_ADN_PLACEMENT_ID, "456")//渠道的广告位id
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMAdFilter.KEY_CHANNEL_ID, "19")//快手渠道id
//                .in(WMAdFilter.KEY_ADN_PLACEMENT_ID, Arrays.asList("123", "456", "789"))//渠道的广告位集合
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMAdFilter.KEY_CHANNEL_ID, "21")//百度渠道id
//                .or()//或的关系：开启一个新的过滤表达式了
//                .in(WMAdFilter.KEY_ADN_PLACEMENT_ID, Arrays.asList("123", "456", "789"))
//                .or()//或的关系：开启一个新的过滤表达式了
//                .in(WMAdFilter.KEY_CHANNEL_ID, Arrays.asList("19", "13", "16"))
//                .or()//或的关系：开启一个新的过滤表达式了
//                .in(WMAdFilter.KEY_BIDDING_TYPE, Arrays.asList(WMAdFilter.C2S, WMAdFilter.S2S, WMAdFilter.NORMAL))
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMAdFilter.KEY_BIDDING_TYPE, WMAdFilter.C2S)
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMAdFilter.KEY_BIDDING_TYPE, WMAdFilter.S2S)
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMAdFilter.KEY_BIDDING_TYPE, WMAdFilter.NORMAL)
//                .or()//或的关系：开启一个新的过滤表达式了
//                .greaterThanEqual(WMAdFilter.KEY_E_CPM, "50") //大于等于50
//                .lessThanEqual(WMAdFilter.KEY_E_CPM, "100") //且小于等于100
//                .or()//或的关系：开启一个新的过滤表达式了
//                .lessThanEqual(WMAdFilter.KEY_E_CPM, "50")
//                .or()//或的关系：开启一个新的过滤表达式了
//                .greaterThanEqual(WMAdFilter.KEY_E_CPM, "100")
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMAdFilter.KEY_E_CPM, "50");
//        windRewardedVideoAd.setFilter(wmFilter);

        windRewardedVideoAd.loadAd();
    }

    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.REWARD_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.REWARD_CALLBACK[i], "", false, false));
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