package com.windmill.android.demo.custom;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.base.WMAdapterError;
import com.windmill.sdk.base.WMLogUtil;
import com.windmill.sdk.custom.WMCustomNativeAdapter;
import com.windmill.sdk.natives.WMNativeAdData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PangleNativeExpressAd extends PangleNativeAd {

    private List<WMNativeAdData> nativeAdDataList = new ArrayList<>();
    private AdListener adListener;
    private WMCustomNativeAdapter adAdapter;

    public PangleNativeExpressAd(WMCustomNativeAdapter adAdapter, AdListener adListener) {
        this.adAdapter = adAdapter;
        this.adListener = adListener;
    }

    @Override
    public void win(double price) {
        if (nativeAdDataList != null && nativeAdDataList.size() > 0) {
            PangleExpressAdData nativeAdData = (PangleExpressAdData) nativeAdDataList.get(0);
            TTNativeExpressAd nativeExpressAd = nativeAdData.getNativeExpressAd();
            if (nativeExpressAd != null) {
                nativeExpressAd.win(price);
            }
        }
    }

    @Override
    public void loss(double price, String lossReason, String winBidder) {
        if (nativeAdDataList != null && nativeAdDataList.size() > 0) {
            PangleExpressAdData nativeAdData = (PangleExpressAdData) nativeAdDataList.get(0);
            TTNativeExpressAd nativeExpressAd = nativeAdData.getNativeExpressAd();
            if (nativeExpressAd != null) {
                nativeExpressAd.loss(price, lossReason, winBidder);
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

            float expressViewWidth = 340;
            float expressViewHeight = 0;//高度设置为0,则高度会自适应
            try {
                if (localExtra != null) {
                    Object w = localExtra.get(WMConstants.AD_WIDTH);
                    if (w != null && (int) w != 0) {
                        expressViewWidth = Float.parseFloat(String.valueOf(w));
                    }
                    Object h = localExtra.get(WMConstants.AD_HEIGHT);
                    if (h != null && (int) h != 0) {
                        expressViewHeight = Float.parseFloat(String.valueOf(h));
                    }
                }
            } catch (Exception e) {
                WMLogUtil.d(WMLogUtil.TAG, "expressViewWidth:" + e.getMessage());
                expressViewWidth = 340;
                expressViewHeight = 0;
            }

            WMLogUtil.d(WMLogUtil.TAG, expressViewWidth + "-----expressViewWidth--------expressViewHeight-------:" + expressViewHeight);

            AdSlot.Builder builder = new AdSlot.Builder()
                    .setCodeId(codeId) //广告位id
                    .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight); //期望模板广告view的size,单位dp

            if (adAdapter.getBiddingType() == WMConstants.AD_TYPE_CLIENT_BIDING) {
                builder.setAdCount(1); //请求广告数量为1到3条
            } else {
                builder.setAdCount(3);//请求广告数量为1到3条
            }

            mTTAdNative.loadNativeExpressAd(builder.build(), new TTAdNative.NativeExpressAdListener() {
                @Override
                public void onError(int code, String message) {
                    WMLogUtil.d(WMLogUtil.TAG, "-----------onError---------:" + code + ":" + message);
                    if (adListener != null) {
                        WMAdapterError adAdapterError = new WMAdapterError(code, message + " codeId " + codeId);
                        adListener.onNativeAdFailToLoad(adAdapterError);
                    }
                }

                @Override
                public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                    if (ads == null || ads.isEmpty()) {
                        if (adListener != null) {
                            WMAdapterError adAdapterError = new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), "ads is null or size be 0 " + codeId);
                            adListener.onNativeAdFailToLoad(adAdapterError);
                        }
                        return;
                    }

                    WMLogUtil.d(WMLogUtil.TAG, "-----------onNativeExpressAdLoad---------" + ads.size());

                    Object price = null;

                    for (int i = 0; i < ads.size(); i++) {

                        TTNativeExpressAd ttNativeExpressAd = ads.get(i);

                        PangleExpressAdData nativeAdData = new PangleExpressAdData(ttNativeExpressAd, adAdapter);

                        nativeAdDataList.add(nativeAdData);

                        if (price == null) {
                            Map<String, Object> mediaExtraInfo = ttNativeExpressAd.getMediaExtraInfo();
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
                adListener.onNativeAdFailToLoad(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), e.getMessage()));
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
