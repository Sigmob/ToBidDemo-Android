package com.windmill.android.demo.custom;

import com.windmill.sdk.base.WMAdapterError;
import com.windmill.sdk.natives.WMNativeAdData;

import java.util.List;
import java.util.Map;

public abstract class PangleNativeAd {

    public abstract void loadAd(String codeId, Map<String, Object> localExtra, Map<String, Object> serverExtra);

    public abstract boolean isReady();

    public abstract void destroy();

    public abstract void win(double price);

    public abstract void loss(double price, String lossReason, String winBidder);

    public abstract List<WMNativeAdData> getNativeAdDataList();

    public interface AdListener {

        void onNativeAdLoadSuccess(List<WMNativeAdData> nativeAdDataList, Object price);

        void onNativeAdFailToLoad(WMAdapterError error);

    }

}
