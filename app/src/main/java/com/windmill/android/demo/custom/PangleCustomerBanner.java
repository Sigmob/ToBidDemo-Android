package com.windmill.android.demo.custom;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.base.WMAdapterError;
import com.windmill.sdk.custom.WMCustomBannerAdapter;
import com.windmill.sdk.models.BidPrice;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;


public class PangleCustomerBanner extends WMCustomBannerAdapter {

    private String TAG = PangleCustomerBanner.this.getClass().getSimpleName();
    private TTNativeExpressAd mTTAd;
    private View bannerView;

    /**
     * @param activity
     * @param localExtra
     * @param serverExtra Placement:CustomInfo:{"codeId":"948728682","adSize":"600x300"}
     */
    @Override
    public void loadAd(final Activity activity, Map<String, Object> localExtra, Map<String, Object> serverExtra) {
        try {
            String placementCustomInfo = (String) serverExtra.get(WMConstants.CUSTOM_INFO);

            JSONObject object = new JSONObject(placementCustomInfo);

//            String codeId = object.optString("codeId");//901121246
            String adSize = object.optString("adSize");//600x300

            /**
             * 广告位Id可以直接取、也可以放到自定义参数里面自己取
             * 平台填写时尽量对应
             */
            String codeId = (String) serverExtra.get(WMConstants.PLACEMENT_ID);//901121246

            float width = 0, height = 0;
            try {
                int x = adSize.indexOf("x");
                width = Float.parseFloat(adSize.substring(0, x));
                height = Float.parseFloat(adSize.substring(x + 1));
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "loadAd:" + codeId + ":" + width + ":" + height);

            if (activity == null || width <= 0 || height <= 0) {
                callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), "loadAd with activity is null or adSize is error"));
                return;
            }

            TTAdNative mTTAdNative = PangleCustomerProxy.getTTAdNative();

            AdSlot.Builder builder = new AdSlot.Builder()
                    .setCodeId(codeId)
                    .setExpressViewAcceptedSize(width / 2, height / 2)//期望模板广告view的size,单位dp
                    .setAdCount(1); //请求广告数量为1到3条

            mTTAdNative.loadBannerExpressAd(builder.build(), new TTAdNative.NativeExpressAdListener() {

                @Override
                public void onError(int code, String message) {
                    Log.d(TAG, "onError:" + code + ":" + message);
                    callLoadFail(new WMAdapterError(code, message));
                }

                @Override
                public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                    if (ads == null || ads.isEmpty()) {
                        callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), "ads is null or size be 0"));
                        return;
                    }
                    Log.d(TAG, "onNativeExpressAdLoad:" + ads.size());
                    mTTAd = ads.get(0);
//                    mTTAd.setSlideIntervalTime(30 * 1000);
                    bindAdListener(activity, mTTAd);
                }
            });
        } catch (Throwable e) {
            callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), e.getMessage()));
        }
    }

    private void bindAdListener(Activity activity, final TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {

            @Override
            public void onAdClicked(View view, int type) {
                callBannerAdClick();
            }

            @Override
            public void onAdShow(View view, int type) {
                callBannerAdShow();
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                callLoadFail(new WMAdapterError(code, msg));
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                bannerView = view;
                if (getBiddingType() == WMConstants.AD_TYPE_CLIENT_BIDING) {
                    String eCpm = "0";
                    Map<String, Object> mediaExtraInfo = ad.getMediaExtraInfo();
                    if (mediaExtraInfo != null) {
                        Object price = mediaExtraInfo.get("price");
                        if (price != null) {
                            eCpm = String.valueOf(price);
                        }
                    }
                    callLoadBiddingSuccess(new BidPrice(eCpm));
                }

                callLoadSuccess();
            }
        });
        //使用默认模板中默认dislike弹出样式
        ad.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                callBannerAdClosed();
            }

            @Override
            public void onCancel() {

            }

        });

        ad.render();
    }

    @Override
    public void notifyBiddingResult(boolean isWin, String price) {
        Log.d(TAG, "notifyBiddingResult:" + isWin + ":" + price);
        if (mTTAd != null) {
            if (isWin) {
                mTTAd.win(Double.parseDouble(price));
            } else {
                mTTAd.loss(Double.parseDouble(price), "102", "");
            }
        }
    }

    @Override
    public boolean isReady() {
        return (mTTAd != null && bannerView != null);
    }

    @Override
    public View getBannerView() {
        return bannerView;
    }

    @Override
    public void destroyAd() {
        if (mTTAd != null) {
            mTTAd.destroy();
            mTTAd = null;
            bannerView = null;
        }
    }
}
