package com.windmill.android.demo.custom;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.base.WMAdapterError;
import com.windmill.sdk.base.WMLogUtil;
import com.windmill.sdk.custom.WMCustomNativeAdapter;
import com.windmill.sdk.natives.WMNativeAdData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PangleNativeUnifiedAd extends PangleNativeAd {

    private List<WMNativeAdData> nativeAdDataList = new ArrayList<>();
    private AdListener adListener;
    private WMCustomNativeAdapter adAdapter;

    public PangleNativeUnifiedAd(WMCustomNativeAdapter adAdapter, AdListener adListener) {
        this.adAdapter = adAdapter;
        this.adListener = adListener;
    }

    @Override
    public void win(double price) {
        if (nativeAdDataList != null && nativeAdDataList.size() > 0) {
            PangleNativeAdData nativeAdData = (PangleNativeAdData) nativeAdDataList.get(0);
            TTFeedAd ttFeedAd = nativeAdData.getTtFeedAd();
            if (ttFeedAd != null) {
                ttFeedAd.win(price);
            }
        }
    }

    @Override
    public void loss(double price, String lossReason, String winBidder) {
        if (nativeAdDataList != null && nativeAdDataList.size() > 0) {
            PangleNativeAdData nativeAdData = (PangleNativeAdData) nativeAdDataList.get(0);
            TTFeedAd ttFeedAd = nativeAdData.getTtFeedAd();
            if (ttFeedAd != null) {
                ttFeedAd.loss(price, lossReason, winBidder);
            }
        }
    }

    @Override
    public List<WMNativeAdData> getNativeAdDataList() {
        return nativeAdDataList;
    }

    @Override
    public void loadAd(final String codeId, Map<String, Object> localExtra, Map<String, Object> serverExtra) {
        try {
            nativeAdDataList.clear();

            TTAdNative mTTAdNative = PangleCustomerProxy.getTTAdNative();

            AdSlot.Builder builder = new AdSlot.Builder()
                    .setCodeId(codeId)//901121737
                    .setImageAcceptedSize(640, 320)
                    .setSupportDeepLink(true)
                    //可设置为空字符串
                    .setUserID("");

            if (adAdapter.getBiddingType() == WMConstants.AD_TYPE_CLIENT_BIDING) {
                builder.setAdCount(1); //请求广告数量为1到3条
            } else {
                builder.setAdCount(3);//请求广告数量为1到3条
            }

            //step5:请求广告，调用feed广告异步请求接口，加载到广告后，拿到广告素材自定义渲染
            mTTAdNative.loadFeedAd(builder.build(), new TTAdNative.FeedAdListener() {
                @Override
                public void onError(int code, String message) {
                    if (adListener != null) {
                        adListener.onNativeAdFailToLoad(new WMAdapterError(code, message));
                    }
                }

                @Override
                public void onFeedAdLoad(final List<TTFeedAd> ads) {
                    if (ads == null || ads.isEmpty()) {
                        if (adListener != null) {
                            WMAdapterError adAdapterError = new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), "ads is null or size be 0 " + codeId);
                            adListener.onNativeAdFailToLoad(adAdapterError);
                        }
                        return;
                    }

                    WMLogUtil.d(WMLogUtil.TAG, "-------------onFeedAdLoad-----------" + ads.size());
                    Object price = null;
                    for (int i = 0; i < ads.size(); i++) {
                        final TTFeedAd ad = ads.get(i);

                        PangleNativeAdData nativeAdData = new PangleNativeAdData(ad, adAdapter);
                        nativeAdDataList.add(nativeAdData);

                        if (price == null) {
                            Map<String, Object> mediaExtraInfo = ad.getMediaExtraInfo();
                            if (mediaExtraInfo != null) {
                                price = mediaExtraInfo.get("price");
                            }
                        }
                    }

                    if (adListener != null) {
                        adListener.onNativeAdLoadSuccess(nativeAdDataList, price);
                    }
                }
            });
        } catch (Throwable e) {
            if (adListener != null) {
                WMAdapterError windAdError = new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), e.getMessage());
                adListener.onNativeAdFailToLoad(windAdError);
            }
        }
    }

    @Override
    public boolean isReady() {
        return nativeAdDataList.size() > 0;
    }

    @Override
    public void destroy() {

    }

}
