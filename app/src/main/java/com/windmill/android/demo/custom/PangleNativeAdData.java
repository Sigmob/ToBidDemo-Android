package com.windmill.android.demo.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.base.WMLogUtil;
import com.windmill.sdk.custom.WMCustomNativeAdapter;
import com.windmill.sdk.natives.WMNativeAdContainer;
import com.windmill.sdk.natives.WMNativeAdData;
import com.windmill.sdk.natives.WMNativeAdDataType;
import com.windmill.sdk.natives.WMNativeAdRender;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class PangleNativeAdData implements WMNativeAdData {

    private TTFeedAd ttFeedAd;
    private NativeAdInteractionListener nativeAdListener;
    private WMCustomNativeAdapter adAdapter;
    private Map<TTFeedAd, TTAppDownloadListener> mTTAppDownloadListenerMap = new WeakHashMap<>();
    private NativeADMediaListener nativeADMediaListener;
    private DislikeInteractionCallback dislikeInteractionCallback;
    private Activity activity;
    private WMNativeAdData nativeAdData = this;

    public PangleNativeAdData(TTFeedAd ttFeedAd, WMCustomNativeAdapter adAdapter) {
        this.ttFeedAd = ttFeedAd;
        this.adAdapter = adAdapter;
    }

    @Override
    public int getInteractionType() {
        if (ttFeedAd != null) {
            switch (ttFeedAd.getInteractionType()) {
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
    public boolean isExpressAd() {
        return false;
    }

    @Override
    public boolean isNativeDrawAd() {
        return false;
    }

    @Override
    public String getCTAText() {
        if (ttFeedAd != null) {
            switch (ttFeedAd.getInteractionType()) {
                case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                    return "开始下载";
                case TTAdConstant.INTERACTION_TYPE_DIAL:
                    return "立即拨打";
                case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
                case TTAdConstant.INTERACTION_TYPE_BROWSER:
                    return "查看详情";
                default:
                    return ttFeedAd.getButtonText();
            }
        }
        return "";
    }

    public TTFeedAd getTtFeedAd() {
        return ttFeedAd;
    }

    @Override
    public String getTitle() {
        if (ttFeedAd != null) {
            return ttFeedAd.getTitle();
        }
        return "";
    }

    @Override
    public String getDesc() {
        if (ttFeedAd != null) {
            return ttFeedAd.getDescription();
        }
        return "";
    }

    @Override
    public Bitmap getAdLogo() {
        if (ttFeedAd != null) {
            return ttFeedAd.getAdLogo();
        }
        return null;
    }

    @Override
    public String getIconUrl() {
        if (ttFeedAd != null) {
            TTImage icon = ttFeedAd.getIcon();
            if (icon != null && icon.isValid()) {
                return icon.getImageUrl();
            }
        }
        return "";
    }

    @Override
    public View getAdChoice() {
        return null;
    }

    @Override
    public int getNetworkId() {
        if (adAdapter != null) {
            return adAdapter.getChannelId();
        }
        return 0;
    }

    @Override
    public int getAdPatternType() {
        if (ttFeedAd != null) {
            if (ttFeedAd.getImageMode() == TTAdConstant.IMAGE_MODE_SMALL_IMG) {
                return WMNativeAdDataType.NATIVE_SMALL_IMAGE_AD;
            } else if (ttFeedAd.getImageMode() == TTAdConstant.IMAGE_MODE_LARGE_IMG || ttFeedAd.getImageMode() == TTAdConstant.IMAGE_MODE_VERTICAL_IMG) {
                return WMNativeAdDataType.NATIVE_BIG_IMAGE_AD;
            } else if (ttFeedAd.getImageMode() == TTAdConstant.IMAGE_MODE_GROUP_IMG) {
                return WMNativeAdDataType.NATIVE_GROUP_IMAGE_AD;
            } else if (ttFeedAd.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO || ttFeedAd.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO_VERTICAL) {
                return WMNativeAdDataType.NATIVE_VIDEO_AD;
            } else {
                return WMNativeAdDataType.NATIVE_UNKNOWN;
            }
        }

        return WMNativeAdDataType.NATIVE_UNKNOWN;
    }

    private WeakReference<Activity> activityWeakReference;

    @Override
    public void connectAdToView(Activity activity, WMNativeAdContainer adContainer, WMNativeAdRender adRender) {
        activityWeakReference = new WeakReference<>(activity);
        if (adRender != null) {
            View view = adRender.createView(activity, getAdPatternType());
            adRender.renderAdView(view, PangleNativeAdData.this);
            if (adContainer != null) {
                adContainer.removeAllViews();
                adContainer.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    @Override
    public void bindViewForInteraction(Context context, View view, List<View> clickableViews, List<View> creativeViewList, View disLikeView) {
        if (ttFeedAd != null) {
            ttFeedAd.registerViewForInteraction((ViewGroup) view, clickableViews, creativeViewList, disLikeView, new TTNativeAd.AdInteractionListener() {
                @Override
                public void onAdClicked(View view, TTNativeAd ad) {
                    if (nativeAdListener != null && adAdapter != null) {
                        nativeAdListener.onADClicked(adAdapter.getAdInFo());
                    }
                    if (ad != null) {
                        WMLogUtil.d(WMLogUtil.TAG, "onAdClicked:" + ad.getTitle());
                    }
                    if (adAdapter != null) {
                        adAdapter.callNativeAdClick(nativeAdData);
                    }
                }

                @Override
                public void onAdCreativeClick(View view, TTNativeAd ad) {
                    if (nativeAdListener != null && adAdapter != null) {
                        nativeAdListener.onADClicked(adAdapter.getAdInFo());
                    }
                    if (ad != null) {
                        WMLogUtil.d(WMLogUtil.TAG, "onAdCreativeClick:" + ad.getTitle());
                    }
                    if (adAdapter != null) {
                        adAdapter.callNativeAdClick(nativeAdData);
                    }
                }

                @Override
                public void onAdShow(TTNativeAd ad) {
                    if (nativeAdListener != null && adAdapter != null) {
                        nativeAdListener.onADExposed(adAdapter.getAdInFo());
                    }
                    if (ad != null) {
                        WMLogUtil.d(WMLogUtil.TAG, "onAdShow:" + ad.getTitle());
                    }
                    if (adAdapter != null) {
                        adAdapter.callNativeAdShow(nativeAdData);
                    }
                }
            });

            if (ttFeedAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                if (activityWeakReference != null && activityWeakReference.get() != null) {
                    ttFeedAd.setActivityForDownloadApp(activityWeakReference.get());
                }
            }

            if (disLikeView != null && activity != null) {
                // 使用默认Dislike
                final TTAdDislike ttAdDislike = ttFeedAd.getDislikeDialog(activity);
                if (ttAdDislike != null) {
                    ttAdDislike.setDislikeInteractionCallback(new TTAdDislike.DislikeInteractionCallback() {
                        @Override
                        public void onShow() {
                            if (dislikeInteractionCallback != null) {
                                dislikeInteractionCallback.onShow();
                            }
                        }

                        @Override
                        public void onSelected(int position, String value, boolean enforce) {
                            if (dislikeInteractionCallback != null) {
                                dislikeInteractionCallback.onSelected(position, value, enforce);
                            }
                        }

                        @Override
                        public void onCancel() {
                            if (dislikeInteractionCallback != null) {
                                dislikeInteractionCallback.onCancel();
                            }
                        }
                    });
                }
                disLikeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ttAdDislike != null)
                            ttAdDislike.showDislikeDialog();
                    }
                });
            }
        }
    }

    @Override
    public void bindImageViews(Context context, List<ImageView> imageViews, int imageRes) {
        if (ttFeedAd != null && !imageViews.isEmpty()) {
            //需要自己渲染Image
            int imageMode = ttFeedAd.getImageMode();

            if (imageMode == TTAdConstant.IMAGE_MODE_SMALL_IMG || imageMode == TTAdConstant.IMAGE_MODE_LARGE_IMG || imageMode == TTAdConstant.IMAGE_MODE_VERTICAL_IMG) {
                if (ttFeedAd.getImageList() != null) {
                    TTImage image = ttFeedAd.getImageList().get(0);
                    if (image != null && image.isValid()) {
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(imageRes);
                        requestOptions.error(imageRes);
                        Glide.with(context).load(image.getImageUrl()).apply(requestOptions).into(imageViews.get(0));
                    }
                }
            } else if (ttFeedAd.getImageMode() == TTAdConstant.IMAGE_MODE_GROUP_IMG) {

                if (ttFeedAd.getImageList() != null) {

                    //取两个list的最小值
                    int min = Math.min(imageViews.size(), ttFeedAd.getImageList().size());

                    for (int i = 0; i < min; i++) {
                        TTImage image = ttFeedAd.getImageList().get(i);
                        if (image != null && image.isValid()) {
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.placeholder(imageRes);
                            requestOptions.error(imageRes);
                            Glide.with(context).load(image.getImageUrl()).apply(requestOptions).into(imageViews.get(i));
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<String> getImageUrlList() {
        if (ttFeedAd != null) {
            //需要自己渲染Image
            List<TTImage> imageInfo = ttFeedAd.getImageList();
            if (imageInfo != null && imageInfo.size() > 0) {
                List<String> urls = new ArrayList<>();
                for (int i = 0; i < imageInfo.size(); i++) {
                    TTImage info = imageInfo.get(i);
                    if (!TextUtils.isEmpty(info.getImageUrl())) {
                        urls.add(info.getImageUrl());
                    }
                }
                return urls;
            }
        }
        return null;
    }

    @Override
    public void setAdLogoParams(FrameLayout.LayoutParams layoutParams) {

    }

    @Override
    public void setInteractionListener(NativeAdInteractionListener adInteractionListener) {
        if (adInteractionListener != null) {
            this.nativeAdListener = adInteractionListener;
        }
    }

    @Override
    public void bindMediaView(Context context, final ViewGroup mediaLayout) {
        if (ttFeedAd != null) {
            //视频广告设置播放状态回调（可选）
            ttFeedAd.setVideoAdListener(new TTFeedAd.VideoAdListener() {
                @Override
                public void onVideoLoad(TTFeedAd ad) {
                    WMLogUtil.d(WMLogUtil.TAG, "onVideoLoad:" + ad.getTitle());
                    if (nativeADMediaListener != null) {
                        nativeADMediaListener.onVideoLoad();
                    }
                }

                @Override
                public void onVideoError(int errorCode, int extraCode) {
                    WMLogUtil.d(WMLogUtil.TAG, "onVideoError:" + errorCode + ":" + extraCode);
                    if (nativeADMediaListener != null) {
                        WindMillError windMillError = WindMillError.ERROR_AD_PLAY;
                        windMillError.setMessage(errorCode + "-" + extraCode);
                        nativeADMediaListener.onVideoError(windMillError);
                    }
                }

                @Override
                public void onVideoAdStartPlay(TTFeedAd ad) {
                    WMLogUtil.d(WMLogUtil.TAG, "onVideoAdStartPlay:" + ad.getTitle());
                    if (nativeADMediaListener != null) {
                        nativeADMediaListener.onVideoStart();
                    }
                }

                @Override
                public void onVideoAdPaused(TTFeedAd ad) {
                    WMLogUtil.d(WMLogUtil.TAG, "onVideoAdPaused:" + ad.getTitle());
                    if (nativeADMediaListener != null) {
                        nativeADMediaListener.onVideoPause();
                    }
                }

                @Override
                public void onVideoAdContinuePlay(TTFeedAd ad) {
                    WMLogUtil.d(WMLogUtil.TAG, "onVideoAdContinuePlay:" + ad.getTitle());
                    if (nativeADMediaListener != null) {
                        nativeADMediaListener.onVideoResume();
                    }
                }

                @Override
                public void onProgressUpdate(long current, long duration) {
                    WMLogUtil.d(WMLogUtil.TAG, "onProgressUpdate current:" + current + " duration:" + duration);
                }

                @Override
                public void onVideoAdComplete(TTFeedAd ad) {
                    WMLogUtil.d(WMLogUtil.TAG, "onVideoAdComplete");
                    if (nativeADMediaListener != null) {
                        nativeADMediaListener.onVideoCompleted();
                    }
                }
            });
            if (mediaLayout != null) {
                View adView = ttFeedAd.getAdView();
                ViewGroup.LayoutParams layoutParams = adView.getLayoutParams();

                //获取视频播放view,该view SDK内部渲染，在媒体平台可配置视频是否自动播放等设置。
                mediaLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = mediaLayout.getWidth();
                        int videoWidth = ttFeedAd.getAdViewWidth();
                        int videoHeight = ttFeedAd.getAdViewHeight();

                        // 根据广告View的宽高比，将adViewHolder.videoView的高度动态改变
                        setViewSize(mediaLayout, width, (int) (width / (videoWidth / (double) videoHeight)));
                        View video = ttFeedAd.getAdView();
                        if (video != null) {
                            if (video.getParent() == null) {
                                mediaLayout.removeAllViews();
                                mediaLayout.addView(video);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void setMediaListener(NativeADMediaListener nativeADMediaListener) {
        if (nativeADMediaListener != null) {
            this.nativeADMediaListener = nativeADMediaListener;
        }
    }

    @Override
    public void setDislikeInteractionCallback(Activity activity, final DislikeInteractionCallback dislikeInteractionCallback) {
        //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
        if (activity != null) {
            this.activity = activity;
        }
        if (dislikeInteractionCallback != null) {
            this.dislikeInteractionCallback = dislikeInteractionCallback;
        }
    }

    @Override
    public void setDownloadListener(final AppDownloadListener appDownloadListener) {
        if (ttFeedAd != null && ttFeedAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {

            if (activityWeakReference != null && activityWeakReference.get() != null) {
                ttFeedAd.setActivityForDownloadApp(activityWeakReference.get());
            }

            TTAppDownloadListener downloadListener = new TTAppDownloadListener() {
                @Override
                public void onIdle() {
                    if (!isValid()) {
                        return;
                    }
                    if (appDownloadListener != null) {
                        appDownloadListener.onIdle();
                    }
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    if (appDownloadListener != null) {
                        appDownloadListener.onDownloadActive(totalBytes, currBytes, fileName, appName);
                    }
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    if (appDownloadListener != null) {
                        appDownloadListener.onDownloadPaused(totalBytes, currBytes, fileName, appName);
                    }
                }

                @Override
                public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    if (appDownloadListener != null) {
                        appDownloadListener.onDownloadFailed(totalBytes, currBytes, fileName, appName);
                    }
                }

                @Override
                public void onInstalled(String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    if (appDownloadListener != null) {
                        appDownloadListener.onInstalled(fileName, appName);
                    }
                }

                @Override
                public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    if (appDownloadListener != null) {
                        appDownloadListener.onDownloadFinished(totalBytes, fileName, appName);
                    }
                }

                @SuppressWarnings("BooleanMethodIsAlwaysInverted")
                private boolean isValid() {
                    return mTTAppDownloadListenerMap.get(ttFeedAd) == this;
                }

            };
            //一个ViewHolder对应一个downloadListener, isValid判断当前ViewHolder绑定的listener是不是自己
            ttFeedAd.setDownloadListener(downloadListener); // 注册下载监听器
            mTTAppDownloadListenerMap.put(ttFeedAd, downloadListener);
        }
    }

    @Override
    public void destroy() {
        if (ttFeedAd != null) {
            ttFeedAd.destroy();
        }
    }

    public void setViewSize(View view, int width, int height) {
        if (view.getParent() instanceof FrameLayout) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
            lp.width = width;
            lp.height = height;
            view.setLayoutParams(lp);
            view.requestLayout();
        } else if (view.getParent() instanceof RelativeLayout) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
            lp.width = width;
            lp.height = height;
            view.setLayoutParams(lp);
            view.requestLayout();
        } else if (view.getParent() instanceof LinearLayout) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
            lp.width = width;
            lp.height = height;
            view.setLayoutParams(lp);
            view.requestLayout();
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
    public View getExpressAdView() {
        return null;
    }

    @Override
    public void render() {

    }

}
