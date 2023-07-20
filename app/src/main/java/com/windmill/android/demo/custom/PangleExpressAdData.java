package com.windmill.android.demo.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.base.WMAdapterError;
import com.windmill.sdk.base.WMLogUtil;
import com.windmill.sdk.custom.WMCustomNativeAdapter;
import com.windmill.sdk.natives.WMNativeAdContainer;
import com.windmill.sdk.natives.WMNativeAdData;
import com.windmill.sdk.natives.WMNativeAdDataType;
import com.windmill.sdk.natives.WMNativeAdRender;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class PangleExpressAdData implements WMNativeAdData {

    private TTNativeExpressAd mNativeExpressAd;
    private Map<TTNativeExpressAd, TTAppDownloadListener> mTTAppDownloadListenerMap = new WeakHashMap<>();
    private WMCustomNativeAdapter adAdapter;
    private WMNativeAdData nativeAdData = this;

    public PangleExpressAdData(TTNativeExpressAd nativeExpressAd, final WMCustomNativeAdapter adAdapter) {
        this.mNativeExpressAd = nativeExpressAd;
        this.adAdapter = adAdapter;
    }

    /**
     * 根据类型设置video的监听，但是不生效针对头条
     *
     * @return
     */
    @Override
    public int getAdPatternType() {
        if (mNativeExpressAd != null) {
            if (mNativeExpressAd.getImageMode() == TTAdConstant.IMAGE_MODE_SMALL_IMG) {
                return WMNativeAdDataType.NATIVE_SMALL_IMAGE_AD;
            } else if (mNativeExpressAd.getImageMode() == TTAdConstant.IMAGE_MODE_LARGE_IMG || mNativeExpressAd.getImageMode() == TTAdConstant.IMAGE_MODE_VERTICAL_IMG) {
                return WMNativeAdDataType.NATIVE_BIG_IMAGE_AD;
            } else if (mNativeExpressAd.getImageMode() == TTAdConstant.IMAGE_MODE_GROUP_IMG) {
                return WMNativeAdDataType.NATIVE_GROUP_IMAGE_AD;
            } else if (mNativeExpressAd.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO || mNativeExpressAd.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO_VERTICAL) {
                return WMNativeAdDataType.NATIVE_VIDEO_AD;
            } else {
                return WMNativeAdDataType.NATIVE_UNKNOWN;
            }
        }

        return WMNativeAdDataType.NATIVE_UNKNOWN;
    }

    @Override
    public void setInteractionListener(final NativeAdInteractionListener adInteractionListener) {
        if (this.mNativeExpressAd != null) {
            this.mNativeExpressAd.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {

                @Override
                public void onAdClicked(View view, int type) {
                    WMLogUtil.d(WMLogUtil.TAG, "----------onAdClicked-----------:" + type);
                    if (adInteractionListener != null && adAdapter != null) {
                        adInteractionListener.onADClicked(adAdapter.getAdInFo());
                    }

                    if (adAdapter != null) {
                        adAdapter.callNativeAdClick(nativeAdData);
                    }
                }

                @Override
                public void onAdShow(View view, int type) {
                    WMLogUtil.d(WMLogUtil.TAG, "---------------onAdShow-----------:" + type);
                    if (adInteractionListener != null && adAdapter != null) {
                        adInteractionListener.onADExposed(adAdapter.getAdInFo());
                    }

                    if (adAdapter != null) {
                        adAdapter.callNativeAdShow(nativeAdData);
                    }
                }

                @Override
                public void onRenderFail(View view, String msg, int code) {
                    WMLogUtil.d(WMLogUtil.TAG, "-------------onRenderFail-----------:" + msg + ":" + code);
                    if (adInteractionListener != null && adAdapter != null) {
                        WindMillError adError = WindMillError.ERROR_AD_ADAPTER_PLAY;
                        adError.setMessage("code : " + code + " msg : " + msg);
                        adInteractionListener.onADError(adAdapter.getAdInFo(), adError);
                    }

                    if (adAdapter != null) {
                        adAdapter.callNativeAdShowError(new WMAdapterError(code, "tt onRenderFail:" + msg));
                    }
                }

                @Override
                public void onRenderSuccess(View view, float width, float height) {
                    WMLogUtil.d(WMLogUtil.TAG, "-------------onRenderSuccess-----------:" + width + ":" + height);
                    if (adInteractionListener != null && adAdapter != null) {
                        adInteractionListener.onADRenderSuccess(adAdapter.getAdInFo(), view, width, height);
                    }
                }
            });
        }
    }

    /**
     * 根据类型设置下载的监听，针对头条生效
     *
     * @return
     */
    @Override
    public int getInteractionType() {
        if (mNativeExpressAd != null) {
            switch (mNativeExpressAd.getInteractionType()) {
                case 2:
                case 3:
                case 5:
                    return WMConstants.INTERACTION_TYPE_BROWSER;
                case 4:
                    return WMConstants.INTERACTION_TYPE_DOWNLOAD;
            }
        }
        return WMConstants.INTERACTION_TYPE_UNKNOWN;
    }

    @Override
    public View getExpressAdView() {
        if (mNativeExpressAd != null) {
            return mNativeExpressAd.getExpressAdView();
        }
        return null;
    }

    /**
     * tt demo都是render之后才addView
     */
    @Override
    public void render() {
        if (mNativeExpressAd != null) {
            mNativeExpressAd.render();
        }
    }

    @Override
    public boolean isExpressAd() {
        return true;
    }

    @Override
    public boolean isNativeDrawAd() {
        return false;
    }

    @Override
    public void setDislikeInteractionCallback(Activity activity, final DislikeInteractionCallback dislikeInteractionCallback) {
        //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
        if (mNativeExpressAd != null) {
            //使用默认模板中默认dislike弹出样式
            mNativeExpressAd.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onShow() {
                    WMLogUtil.d(WMLogUtil.TAG, "---------------onShow-----------");
                    if (dislikeInteractionCallback != null) {
                        dislikeInteractionCallback.onShow();
                    }
                }

                @Override
                public void onSelected(int position, String value, boolean enforce) {
                    WMLogUtil.d(WMLogUtil.TAG, "---------------onSelected-----------:" + position + ":" + value + ":" + enforce);
                    if (dislikeInteractionCallback != null) {
                        dislikeInteractionCallback.onSelected(position, value, enforce);
                    }
                }

                @Override
                public void onCancel() {
                    WMLogUtil.d(WMLogUtil.TAG, "---------------onCancel-----------");
                    if (dislikeInteractionCallback != null) {
                        dislikeInteractionCallback.onCancel();
                    }
                }
            });
        }
    }

    @Override
    public void setDownloadListener(final AppDownloadListener appDownloadListener) {
        if (mNativeExpressAd != null && mNativeExpressAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            TTAppDownloadListener downloadListener = new TTAppDownloadListener() {
                @Override
                public void onIdle() {
                    if (!isValid()) {
                        return;
                    }
                    WMLogUtil.d(WMLogUtil.TAG, "---------------onIdle-----------");
                    if (appDownloadListener != null) {
                        appDownloadListener.onIdle();
                    }
                }

                @Override
                public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    WMLogUtil.d(WMLogUtil.TAG, "---------------onDownloadActive-----------");
                    if (appDownloadListener != null) {
                        appDownloadListener.onDownloadActive(totalBytes, currBytes, fileName, appName);
                    }
                }

                @Override
                public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    WMLogUtil.d(WMLogUtil.TAG, "---------------onDownloadPaused-----------");
                    if (appDownloadListener != null) {
                        appDownloadListener.onDownloadPaused(totalBytes, currBytes, fileName, appName);
                    }
                }

                @Override
                public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    WMLogUtil.d(WMLogUtil.TAG, "---------------onDownloadFailed-----------");
                    if (appDownloadListener != null) {
                        appDownloadListener.onDownloadFailed(totalBytes, currBytes, fileName, appName);
                    }
                }

                @Override
                public void onInstalled(String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    WMLogUtil.d(WMLogUtil.TAG, "---------------onInstalled-----------");
                    if (appDownloadListener != null) {
                        appDownloadListener.onInstalled(fileName, appName);
                    }
                }

                @Override
                public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    WMLogUtil.d(WMLogUtil.TAG, "---------------onDownloadFinished-----------");
                    if (appDownloadListener != null) {
                        appDownloadListener.onDownloadFinished(totalBytes, fileName, appName);
                    }
                }

                @SuppressWarnings("BooleanMethodIsAlwaysInverted")
                private boolean isValid() {
                    return mTTAppDownloadListenerMap.get(mNativeExpressAd) == this;
                }
            };
            //一个ViewHolder对应一个downloadListener, isValid判断当前ViewHolder绑定的listener是不是自己
            mNativeExpressAd.setDownloadListener(downloadListener);
            mTTAppDownloadListenerMap.put(mNativeExpressAd, downloadListener);
        }
    }

    public TTNativeExpressAd getNativeExpressAd() {
        return mNativeExpressAd;
    }

    @Override
    public int getNetworkId() {
        if (adAdapter != null) {
            return adAdapter.getChannelId();
        }
        return 0;
    }

    @Override
    public void destroy() {
        if (mNativeExpressAd != null) {
            mNativeExpressAd.destroy();
        }
    }

    @Override
    public void startVideo() {

    }

    @Override
    public void pauseVideo() {

    }

    @Override
    public void resumeVideo() {

    }

    @Override
    public void stopVideo() {

    }

    @Override
    public void bindMediaView(Context context, ViewGroup mediaLayout) {

    }

    @Override
    public void connectAdToView(Activity activity, WMNativeAdContainer adContainer, WMNativeAdRender adRender) {

    }

    @Override
    public void bindImageViews(Context context, List<ImageView> imageViews, int defaultImageRes) {

    }

    @Override
    public List<String> getImageUrlList() {
        return null;
    }

    @Override
    public void setAdLogoParams(FrameLayout.LayoutParams layoutParams) {

    }

    @Override
    public void bindViewForInteraction(Context context, View view, List<View> clickableViews, List<View> creativeViewList, View disLikeView) {

    }

    /**
     * 头条暂时没有这个监听
     *
     * @param nativeADMediaListener
     */
    @Override
    public void setMediaListener(NativeADMediaListener nativeADMediaListener) {

    }

    @Override
    public String getCTAText() {
        return "";
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public String getDesc() {
        return "";
    }

    @Override
    public Bitmap getAdLogo() {
        return null;
    }

    @Override
    public String getIconUrl() {
        return "";
    }

    @Override
    public View getAdChoice() {
        return null;
    }
}
