package com.windmill.android.demo.custom;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.base.WMAdapterError;
import com.windmill.sdk.custom.WMCustomRewardAdapter;
import com.windmill.sdk.models.BidPrice;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PangleCustomerReward extends WMCustomRewardAdapter {

    private String TAG = PangleCustomerReward.this.getClass().getSimpleName();
    private TTRewardVideoAd ttRewardVideoAd;

    /**
     * @param activity
     * @param localExtra
     * @param serverExtra Placement:CustomInfo:{"codeId":"901121365"}
     */
    @Override
    public void loadAd(Activity activity, Map<String, Object> localExtra, Map<String, Object> serverExtra) {
        try {
            String placementCustomInfo = (String) serverExtra.get(WMConstants.CUSTOM_INFO);

            JSONObject object = new JSONObject(placementCustomInfo);

//            String codeId = object.optString("codeId");//901121365
            /**
             * 广告位Id可以直接取、也可以放到自定义参数里面自己取
             * 平台填写时尽量对应
             */
            String codeId = (String) serverExtra.get(WMConstants.PLACEMENT_ID);//901121365

            Log.d(TAG, "loadAd:" + codeId);

            TTAdNative.RewardVideoAdListener rewardVideoAdListener = new TTAdNative.RewardVideoAdListener() {

                @Override
                public void onError(int code, String message) {
                    Log.d(TAG, "onError " + code + ":" + message);
                    callLoadFail(new WMAdapterError(code, message));
                }

                @Override
                public void onRewardVideoCached() {

                }

                //视频广告加载后的视频文件资源缓存到本地的回调
                @Override
                public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {
                    Log.d(TAG, "onRewardVideoCached");
                    callLoadSuccess();
                }

                //=，如title,视频url等，不包括视频文件
                @Override
                public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                    Log.d(TAG, "onRewardVideoAdLoad");
                    ttRewardVideoAd = ad;

                    ad.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                        @Override
                        public void onAdShow() {
                            callVideoAdShow();
                        }

                        @Override
                        public void onAdVideoBarClick() {
                            callVideoAdClick();
                        }

                        @Override
                        public void onAdClose() {
                            callVideoAdClosed();
                        }

                        @Override
                        public void onVideoComplete() {
                            callVideoAdPlayComplete();
                        }

                        @Override
                        public void onVideoError() {
                            callVideoAdPlayError(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_PLAY.getErrorCode(), "tt onVideoError"));
                        }

                        @Override
                        public void onRewardVerify(boolean b, int i, String s, int i1, String s1) {

                        }

                        @Override
                        public void onRewardArrived(boolean b, int i, Bundle bundle) {
                            callVideoAdReward(b);
                        }

                        @Override
                        public void onSkippedVideo() {
                            callVideoAdSkipped();
                        }
                    });

                    if (getBiddingType() == WMConstants.AD_TYPE_CLIENT_BIDING) {
                        String eCpm = "0";
                        Map<String, Object> mediaExtraInfo = ttRewardVideoAd.getMediaExtraInfo();
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
            mTTAdNative.loadRewardVideoAd(builder.build(), rewardVideoAdListener);
        } catch (Throwable e) {
            callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), e.getMessage()));
        }
    }

    @Override
    public void showAd(Activity activity, HashMap<String, String> localExtra, Map<String, Object> serverExtra) {
        try {
            if (ttRewardVideoAd != null) {
                if (getBiddingType() == WMConstants.AD_TYPE_CLIENT_BIDING) {
                    Object eCpm = serverExtra.get(WMConstants.E_CPM);
                    if (eCpm != null) {
                        ttRewardVideoAd.setPrice(Double.parseDouble((String) eCpm));
                    }
                }
                ttRewardVideoAd.showRewardVideoAd(activity);
            } else {
                callVideoAdPlayError(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_PLAY.getErrorCode(), "成功加载广告后再进行广告展示!"));
            }
        } catch (Throwable e) {
            callVideoAdPlayError(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_PLAY.getErrorCode(), e.getMessage()));
        }
    }

    @Override
    public void notifyBiddingResult(boolean isWin, String price) {
        Log.d(TAG, "notifyBiddingResult " + isWin + ":" + price);
        if (ttRewardVideoAd != null) {
            if (isWin) {
                ttRewardVideoAd.win(Double.parseDouble(price));
            } else {
                ttRewardVideoAd.loss(Double.parseDouble(price), "102", "");
            }
        }
    }

    @Override
    public boolean isReady() {
        return ttRewardVideoAd != null && (SystemClock.elapsedRealtime() < ttRewardVideoAd.getExpirationTimestamp());
    }

    @Override
    public void destroyAd() {
        if (ttRewardVideoAd != null) {
            ttRewardVideoAd = null;
        }
    }

}
