package com.windmill.android.demo.custom;

import android.content.Context;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillAd;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.custom.WMCustomAdapterProxy;

import org.json.JSONObject;

import java.util.Map;

public class PangleCustomerProxy extends WMCustomAdapterProxy {

    private String TAG = PangleCustomerProxy.this.getClass().getSimpleName();
    private TTAdConfig mTTAdConfig;
    private static TTAdNative mTTAdNative;

    /**
     * @param context
     * @param serverExtra APP:CustomInfo:{"appId":"5001121"}
     */
    @Override
    public void initializeADN(final Context context, Map<String, Object> serverExtra) {
        try {
            String appCustomInfo = (String) serverExtra.get(WMConstants.CUSTOM_INFO);

            JSONObject object = new JSONObject(appCustomInfo);

            String appId = object.optString("appId");

            Log.d(TAG, "initializeADN:" + appId);

            mTTAdConfig = new TTAdConfig.Builder()
                    .appId(appId)
                    .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                    .allowShowNotify(true) //是否允许sdk展示通知栏提示
                    .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                    //允许直接下载的网络状态集合,没有设置的网络下点击下载apk会有二次确认弹窗，弹窗中会披露应用信息
                    .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G) //允许直接下载的网络状态集合
                    .supportMultiProcess(false)//是否支持多进程
                    .build();

            //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
            TTAdSdk.init(context, mTTAdConfig, new TTAdSdk.InitCallback() {
                @Override
                public void success() {
                    Log.d(TAG, "success");
                    callInitSuccess();
                }

                @Override
                public void fail(int code, String msg) {
                    Log.d(TAG, "fail:" + code + ":" + msg);
                    callInitFail(code, msg);
                }
            });

            mTTAdNative = TTAdSdk.getAdManager().createAdNative(context.getApplicationContext());
        } catch (Throwable e) {
            e.printStackTrace();
            callInitFail(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), e.getMessage());
        }
    }

    public static TTAdNative getTTAdNative() {
        return mTTAdNative;
    }

    @Override
    public String getNetworkSdkVersion() {
        return TTAdSdk.getAdManager().getSDKVersion();
    }

    @Override
    public int baseOnToBidCustomAdapterVersion() {
        return WMConstants.TO_BID_CUSTOM_ADAPTER_VERSION_1;
    }

    @Override
    public void notifyPrivacyStatusChange() {
        Log.d(TAG, "notifyPrivacyStatusChange");
        if (mTTAdConfig != null) {
            String userData = "[{\"name\":\"personal_ads_type\",\"value\":\"0\"}]";
            if (WindMillAd.sharedAds().isPersonalizedAdvertisingOn()) {
                userData = "[{\"name\":\"personal_ads_type\",\"value\":\"1\"}]";
            }
            mTTAdConfig.setData(userData);
            TTAdSdk.updateAdConfig(mTTAdConfig);
        }
    }
}
