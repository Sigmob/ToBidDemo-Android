package com.windmill.android.demo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.windmill.android.demo.log.CallBackInfo;
import com.windmill.android.demo.log.CallBackItem;
import com.windmill.android.demo.log.ExpandAdapter;
import com.windmill.android.demo.splash.SplashZoomOutManager;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.models.AdInfo;
import com.windmill.sdk.splash.IWMSplashEyeAd;
import com.windmill.sdk.splash.WMSplashAd;
import com.windmill.sdk.splash.WMSplashAdListener;
import com.windmill.sdk.splash.WMSplashAdRequest;
import com.windmill.sdk.splash.WMSplashEyeAdListener;

import java.util.ArrayList;
import java.util.List;

public class SplashAdActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;
    private String placementId;
    private ListView listView;
    private ExpandAdapter adapter;
    private ViewGroup splashLY;
    private WMSplashAd splashAd;

    private List<CallBackItem> callBackDataList = new ArrayList<>();

    private void initViewGroup(Activity activity) {

//        if (this.splashLY != null) {
//            if (this.splashLY.getParent() != null) {
//                ((ViewGroup) this.splashLY.getParent()).removeView(this.splashLY);
//            }
//
//            this.splashLY = null;
//        }

        splashLY = new RelativeLayout(activity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        viewGroup.addView(splashLY, layoutParams);
    }

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
        setContentView(R.layout.activity_splash_ad);
        spinner = findViewById(R.id.id_spinner);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.splash_adapter));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        CheckBox fullScreen = findViewById(R.id.cb_fullscreen);
        CheckBox selfLogo = findViewById(R.id.cb_self_logo);
        fullScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = SplashAdActivity.this.getSharedPreferences("setting", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.CONF_FULL_SCREEN, isChecked);
                editor.apply();
            }
        });

        selfLogo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = SplashAdActivity.this.getSharedPreferences("setting", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.CONF_SELF_LOGO, isChecked);
                editor.apply();
            }
        });

        String[] stringArray = getResources().getStringArray(R.array.splash_id_value);
        placementId = stringArray[0];
        initCallBack();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] stringArray = getResources().getStringArray(R.array.splash_id_value);
        placementId = stringArray[position];
        Log.d("lance", "------onItemSelected------" + position + ":" + placementId);
        SharedPreferences sharedPreferences = SplashAdActivity.this.getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.CONF_PLACEMENT_ID, placementId);
        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("lance", "------onNothingSelected------");
    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.bt_load:
                resetCallBackData();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                loadSplashAd();
                break;
            case R.id.bt_show:
                showSplashAd();
                break;
        }
    }

    private void loadSplashAd() {

        initViewGroup(this);

        WMSplashAdRequest adRequest = new WMSplashAdRequest(placementId, String.valueOf(0), null);
        splashAd = new WMSplashAd(this, adRequest, new WMSplashAdListener() {
            @Override
            public void onSplashAdSuccessPresent(AdInfo adInfo) {
                logCallBack("onSplashAdSuccessPresent", "");
            }

            @Override
            public void onSplashAdSuccessLoad(String placementId) {
                logCallBack("onSplashAdSuccessLoad", "");
            }

            @Override
            public void onSplashAdFailToLoad(WindMillError error, String placementId) {
                logCallBack("onSplashAdFailToLoad", error.toString());
                if (splashLY != null) {
                    splashLY.removeAllViews();
                    splashLY.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSplashAdClicked(AdInfo adInfo) {
                logCallBack("onSplashAdClicked", "");
            }

            @Override
            public void onSplashClosed(AdInfo adInfo, IWMSplashEyeAd splashEyeAd) {
                logCallBack("onSplashClosed", "");
                //（1）当穿山甲、优量汇的开屏广告素材支持点睛时，splashEyeAd不为null
                //（2）当展示的是快手开屏广告时，splashEyeAd为非null值，但不一定表示此次快手开屏广告的素材支持点睛，不支持时调用IATSplashEyeAd#show()方法会直接回调ATSplashEyeAdListener#onAdDismiss()方法
                //（3）当splashEyeAd不为null，但是开发者不想支持点睛功能时，必须调用splashEyeAd.destroy()释放资源，然后跳转主页面或者移除开屏View
                if (splashLY != null) {
                    splashLY.removeAllViews();
                    splashLY.setVisibility(View.GONE);
                }
                //展示点睛广告
                showSplashEyeAd(splashEyeAd);
            }
        });

        splashAd.loadAdOnly();
    }

    private void showSplashAd() {
        if (splashAd != null && splashAd.isReady()) {
//            splashAd.showAd(null);
            splashAd.showAd(splashLY);
        } else {
            Toast.makeText(SplashAdActivity.this, "Ad is not Ready", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSplashEyeAd(final IWMSplashEyeAd splashEyeAd) {
        if (splashEyeAd == null) {
            return;
        }
        splashEyeAd.show(this, null, new WMSplashEyeAdListener() {
            @Override
            public void onAnimationStart(View splashView) {
                Log.i("lance", "----------onAnimationStart---------: eye ad");
                //执行缩放动画
                SplashZoomOutManager zoomOutManager = SplashZoomOutManager.getInstance(SplashAdActivity.this.getApplicationContext());

                ////建议优先使用IATSplashEyeAd#getSuggestedSize()返回的大小作为缩放动画的目标大小
                int[] suggestedSize = splashEyeAd.getSuggestedSize(SplashAdActivity.this.getApplicationContext());
                if (suggestedSize != null) {
                    zoomOutManager.setSplashEyeAdViewSize(suggestedSize[0], suggestedSize[1]);
                }

//                zoomOutManager.setSplashInfo(splashView, SplashAdActivity.this.getWindow().getDecorView());
                ViewGroup content = SplashAdActivity.this.findViewById(android.R.id.content);
                zoomOutManager.startZoomOut(splashView, content, content, new SplashZoomOutManager.AnimationCallBack() {

                    @Override
                    public void animationStart(int animationTime) {
                        Log.i("lance", "----------animationStart---------: eye ad");
                    }

                    @Override
                    public void animationEnd() {
                        Log.i("lance", "----------animationEnd---------: eye ad");
                        //当缩放动画完成时必须调用IATSplashEyeAd#onFinished()通知SDK
                        splashEyeAd.onFinished();
                    }
                });
            }

            @Override
            public void onAdDismiss(boolean isSupportEyeSplash) {
                Log.i("lance", "----------onAdDismiss---------:" + isSupportEyeSplash);
                //建议在此回调中调用IATSplashEyeAd#destroy()释放资源以及释放其他资源，以免造成内存泄漏
                SplashZoomOutManager zoomOutManager = SplashZoomOutManager.getInstance(SplashAdActivity.this.getApplicationContext());
                zoomOutManager.clearStaticData();
                splashEyeAd.destroy();
            }
        });
    }

    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.SPLASH_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.SPLASH_CALLBACK[i], "", false, false));
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