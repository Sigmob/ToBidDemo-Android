package com.windmill.android.demo.custom;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.base.WMAdapterError;
import com.windmill.sdk.custom.WMCustomInterstitialAdapter;
import com.windmill.sdk.models.BidPrice;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PangleCustomerInterstitial extends WMCustomInterstitialAdapter {

    private String TAG = PangleCustomerInterstitial.this.getClass().getSimpleName();
    private TTFullScreenVideoAd mTTFullScreenVideoAd;

    /**
     * @param activity
     * @param localExtra
     * @param serverExtra Placement:CustomInfo:{"codeId":"901121375"}
     */
    @Override
    public void loadAd(Activity activity, Map<String, Object> localExtra, Map<String, Object> serverExtra) {
        try {
            String placementCustomInfo = (String) serverExtra.get(WMConstants.CUSTOM_INFO);

            JSONObject object = new JSONObject(placementCustomInfo);

//            String codeId = object.optString("codeId");//901121375
            /**
             * 广告位Id可以直接取、也可以放到自定义参数里面自己取
             * 平台填写时尽量对应
             */
            String codeId = (String) serverExtra.get(WMConstants.PLACEMENT_ID);//901121375

            Log.d(TAG, "loadAd:" + codeId);

            TTAdNative.FullScreenVideoAdListener fullScreenVideoAdListener = new TTAdNative.FullScreenVideoAdListener() {

                @Override
                public void onError(int code, String message) {
                    Log.d(TAG, "onError:" + code + ":" + message);
                    callLoadFail(new WMAdapterError(code, message));
                }

                @Override
                public void onFullScreenVideoCached() {

                }

                //视频广告加载后的视频文件资源缓存到本地的回调
                @Override
                public void onFullScreenVideoCached(TTFullScreenVideoAd ttFullScreenVideoAd) {
                    Log.d(TAG, "onFullScreenVideoCached");
                    callLoadSuccess();
                }

                //=，如title,视频url等，不包括视频文件
                @Override
                public void onFullScreenVideoAdLoad(final TTFullScreenVideoAd ad) {
                    Log.d(TAG, "onFullScreenVideoAdLoad");
                    //mttRewardVideoAd.setShowDownLoadBar(false);
                    mTTFullScreenVideoAd = ad;
                    ad.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                        @Override
                        public void onAdShow() {
                            Log.d(TAG, "onAdShow");
                            callVideoAdShow();
                        }

                        @Override
                        public void onAdVideoBarClick() {
                            Log.d(TAG, "onAdVideoBarClick");
                            callVideoAdClick();
                        }

                        @Override
                        public void onAdClose() {
                            Log.d(TAG, "onAdClose");
                            callVideoAdClosed();
                        }

                        @Override
                        public void onVideoComplete() {
                            Log.d(TAG, "onVideoComplete");
                            callVideoAdPlayComplete();
                        }

                        @Override
                        public void onSkippedVideo() {
                            Log.d(TAG, "onSkippedVideo");
                            callVideoAdSkipped();
                        }
                    });

                    if (mTTFullScreenVideoAd != null && getBiddingType() == WMConstants.AD_TYPE_CLIENT_BIDING) {
                        String eCpm = "0";
                        Map<String, Object> mediaExtraInfo = mTTFullScreenVideoAd.getMediaExtraInfo();
                        if (mediaExtraInfo != null) {
                            Object price = mediaExtraInfo.get("price");

                            if (price != null) {
                                eCpm = String.valueOf(price);
                            }
                        }

                        callLoadBiddingSuccess(new BidPrice(eCpm));
                    }
                }
            };

            TTAdNative mTTAdNative = PangleCustomerProxy.getTTAdNative();

            AdSlot.Builder builder = new AdSlot.Builder()
                    .setCodeId(codeId)
                    .setSupportDeepLink(true)
                    //可设置为空字符串
                    .setUserID("");
            mTTAdNative.loadFullScreenVideoAd(builder.build(), fullScreenVideoAdListener);
        } catch (Throwable e) {
            callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), "catch TT loadAd error " + e.getMessage()));
        }
    }

    @Override
    public void showAd(Activity activity, HashMap<String, String> localExtra, Map<String, Object> serverExtra) {
        try {
            if (mTTFullScreenVideoAd != null) {
                if (getBiddingType() == WMConstants.AD_TYPE_CLIENT_BIDING) {
                    Object eCpm = serverExtra.get(WMConstants.E_CPM);
                    if (eCpm != null) {
                        mTTFullScreenVideoAd.setPrice(Double.parseDouble((String) eCpm));
                    }
                }
                mTTFullScreenVideoAd.showFullScreenVideoAd(activity);
            } else {
                callVideoAdPlayError(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_PLAY.getErrorCode(), "成功加载广告后再进行广告展示！"));
            }
        } catch (Throwable throwable) {
            callVideoAdPlayError(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_PLAY.getErrorCode(), "catch TT presentVideoAd error " + throwable.getMessage()));
        }
    }

    @Override
    public boolean isReady() {
        return mTTFullScreenVideoAd != null && (SystemClock.elapsedRealtime() < mTTFullScreenVideoAd.getExpirationTimestamp());
    }

    @Override
    public void notifyBiddingResult(boolean isWin, String price) {
        Log.d(TAG, "notifyBiddingResult:" + isWin + ":" + price);
        if (mTTFullScreenVideoAd != null) {
            if (isWin) {
                mTTFullScreenVideoAd.win(Double.parseDouble(price));
            } else {
                mTTFullScreenVideoAd.loss(Double.parseDouble(price), "102", "");
            }
        }
    }

    @Override
    public void destroyAd() {
        if (mTTFullScreenVideoAd != null) {
            mTTFullScreenVideoAd = null;
        }
    }
}
