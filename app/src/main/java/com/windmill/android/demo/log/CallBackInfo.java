package com.windmill.android.demo.log;

/**
 * created by lance on   2021/12/9 : 9:22 上午
 */
public class CallBackInfo {

    public static String[] REWARD_CALLBACK = {
            "onVideoAdLoadSuccess",
            "onVideoAdLoadError",
            "onVideoAdPlayStart",
            "onVideoAdPlayError",
            "onVideoAdPlayEnd",
            "onVideoAdClicked",
            "onVideoAdClosed",
            "onVideoRewarded"};

    public static String[] INTERSTITIAL_CALLBACK = {
            "onInterstitialAdLoadSuccess",
            "onInterstitialAdLoadError",
            "onInterstitialAdPlayStart",
            "onInterstitialAdPlayError",
            "onInterstitialAdPlayEnd",
            "onInterstitialAdClicked",
            "onInterstitialAdClosed",};

    public static String[] SPLASH_CALLBACK = {
            "onSplashAdSuccessLoad",
            "onSplashAdFailToLoad",
            "onSplashAdSuccessPresent",
            "onSplashAdClicked",
            "onSplashClosed"};

    public static String[] NATIVE_CALLBACK = {
            "onFeedAdLoad",
            "onError",
            "onADExposed",
            "onADClicked",
            "onADError",
            "onADRenderSuccess"};

    public static String[] BANNER_CALLBACK = {
            "onAdLoadSuccess",
            "onAdLoadError",
            "onAdShown",
            "onAdClicked",
            "onAdClosed",
            "onAdAutoRefreshed",
            "onAdAutoRefreshFail"};

}
