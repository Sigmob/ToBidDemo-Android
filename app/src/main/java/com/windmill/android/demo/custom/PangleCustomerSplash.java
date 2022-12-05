package com.windmill.android.demo.custom;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.CSJAdError;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.base.WMAdapterError;
import com.windmill.sdk.custom.WMCustomSplashAdapter;
import com.windmill.sdk.models.BidPrice;
import com.windmill.sdk.splash.IWMSplashEyeAd;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Map;

public class PangleCustomerSplash extends WMCustomSplashAdapter implements TTAdNative.CSJSplashAdListener, CSJSplashAd.SplashAdListener {

    private String TAG = PangleCustomerSplash.this.getClass().getSimpleName();
    private CSJSplashAd mSplashAD;
    private PangleSplashEyeAd splashEyeAd;
    /**
     * 是否支持开屏点睛
     */
    private boolean isSupportZoomOut = false;
    /**
     * 当广告对象没有isReady()方法时、可自己增加变量记录ready状态
     */
    private boolean isReady = false;

    /**
     * @param activity
     * @param viewGroup
     * @param localExtra
     * @param serverExtra Placement:CustomInfo:{"codeId":"801121648","isSplashEye":true}
     */
    @Override
    public void loadAd(Activity activity, ViewGroup viewGroup, Map<String, Object> localExtra, Map<String, Object> serverExtra) {
        try {
            isReady = false;

            if (activity == null) {
                callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), "activity is null"));
            } else {
                String placementCustomInfo = (String) serverExtra.get(WMConstants.CUSTOM_INFO);

                JSONObject object = new JSONObject(placementCustomInfo);

                isSupportZoomOut = object.optBoolean("isSplashEye");

//                String codeId = object.optString("codeId");//801121648
                /**
                 * 广告位Id可以直接取、也可以放到自定义参数里面自己取
                 * 平台填写时尽量对应
                 */
                String codeId = (String) serverExtra.get(WMConstants.PLACEMENT_ID);//801121648

                Log.d(TAG, "loadAd " + codeId + ":" + isSupportZoomOut);

                TTAdNative ttAdNative = TTAdSdk.getAdManager().createAdNative(activity);

                int splashWidthPx = getRealMetrics(activity).widthPixels;
                int screenHeightPx = getRealMetrics(activity).heightPixels;

                float splashWidthDp = px2dip(activity, splashWidthPx);
                float screenHeightDp = px2dip(activity, screenHeightPx);
                try {
                    Object w = localExtra.get(WMConstants.AD_WIDTH);
                    if (w != null && (int) w != 0) {
                        splashWidthPx = Integer.parseInt(String.valueOf(w));
                        splashWidthDp = px2dip(activity, splashWidthPx);
                    }

                    Object h = localExtra.get(WMConstants.AD_HEIGHT);
                    if (h != null && (int) h != 0) {
                        screenHeightPx = Integer.parseInt(String.valueOf(h));
                        screenHeightDp = px2dip(activity, screenHeightPx);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                AdSlot.Builder builder = new AdSlot.Builder()
                        .setCodeId(codeId)
                        //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
                        //view宽高等于图片的宽高
                        .setExpressViewAcceptedSize(splashWidthDp, screenHeightDp) // 单位是dp
                        .setImageAcceptedSize(splashWidthPx, screenHeightPx) // 单位是px
                        .setSupportDeepLink(true);
                //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
                AdSlot adSlot = builder.build();
                //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
                ttAdNative.loadSplashAd(adSlot, this, 5000);
            }
        } catch (Exception e) {
            callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), e.getMessage()));
        }
    }

    @Override
    public void showAd(Activity activity, ViewGroup viewGroup, Map<String, Object> serverExtra) {
        try {
            if (mSplashAD != null && isReady) {
                if (getBiddingType() == WMConstants.AD_TYPE_CLIENT_BIDING) {
                    Object eCpm = serverExtra.get(WMConstants.E_CPM);
                    if (eCpm != null) {
                        mSplashAD.setPrice(Double.parseDouble((String) eCpm));
                    }
                }
                mSplashAD.showSplashView(viewGroup);
                mSplashAD.setSplashAdListener(this);

                if (isSupportZoomOut) {
                    initSplashClickEyeData(mSplashAD, mSplashAD.getSplashView(), viewGroup);
                }
            } else {
                callSplashAdShowError(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_PLAY.getErrorCode(), "mSplashAD is null"));
            }
            isReady = false;
        } catch (Exception e) {
            callSplashAdShowError(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_PLAY.getErrorCode(), e.getMessage()));
        }
    }


    @Override
    public boolean isReady() {
        return isReady && mSplashAD != null;
    }

    @Override
    public void notifyBiddingResult(boolean isWin, String price) {
        Log.d(TAG, "notifyBiddingResult " + isWin + ":" + price);
        if (mSplashAD != null) {
            if (isWin) {
                mSplashAD.win(Double.parseDouble(price));
            } else {
                mSplashAD.loss(Double.parseDouble(price), "102", "");
            }
        }
    }

    @Override
    public void destroyAd() {
        if (mSplashAD != null && !isSupportZoomOut) {
            mSplashAD.setSplashAdListener(null);
            mSplashAD.setDownloadListener(null);
            mSplashAD.setSplashClickEyeListener(null);
            mSplashAD = null;
            isReady = false;
        }
    }

    @Override
    public IWMSplashEyeAd getSplashEyeAd() {
        return splashEyeAd;
    }

    private void initSplashClickEyeData(final CSJSplashAd splashAd, final View splashView, final ViewGroup viewGroup) {

        if (splashAd == null || splashView == null || viewGroup == null) {
            return;
        }

        splashAd.setSplashClickEyeListener(new CSJSplashAd.SplashClickEyeListener() {
            @Override
            public void onSplashClickEyeReadyToShow(CSJSplashAd csjSplashAd) {
                splashEyeAd = new PangleSplashEyeAd(splashAd, splashView, viewGroup);
            }

            @Override
            public void onSplashClickEyeClick() {
                callSplashAdClick();
            }

            @Override
            public void onSplashClickEyeClose() {
                if (splashEyeAd != null && splashEyeAd.getSplashEyeAdListener() != null) {
                    splashEyeAd.getSplashEyeAdListener().onAdDismiss(true);
                }
            }
        });
    }

    @Override
    public void onSplashLoadSuccess() {
        Log.d(TAG, "onSplashLoadSuccess");
    }

    @Override
    public void onSplashLoadFail(CSJAdError csjAdError) {
        Log.d(TAG, "onSplashLoadFail:" + csjAdError.toString());
        callLoadFail(new WMAdapterError(csjAdError.getCode(), csjAdError.getMsg()));
    }

    @Override
    public void onSplashRenderSuccess(CSJSplashAd csjSplashAd) {
        Log.d(TAG, "onSplashRenderSuccess");
        if (csjSplashAd == null) {
            callLoadFail(new WMAdapterError(WindMillError.ERROR_AD_ADAPTER_LOAD.getErrorCode(), "ttSplashAd is null"));
            return;
        }
        mSplashAD = csjSplashAd;
        isReady = true;

        if (getBiddingType() == WMConstants.AD_TYPE_CLIENT_BIDING) {
            String eCpm = "0";
            Map<String, Object> mediaExtraInfo = csjSplashAd.getMediaExtraInfo();
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

    @Override
    public void onSplashRenderFail(CSJSplashAd csjSplashAd, CSJAdError csjAdError) {
        callLoadFail(new WMAdapterError(csjAdError.getCode(), csjAdError.getMsg()));
    }

    @Override
    public void onSplashAdShow(CSJSplashAd csjSplashAd) {
        callSplashAdShow();
    }

    @Override
    public void onSplashAdClick(CSJSplashAd csjSplashAd) {
        callSplashAdClick();
    }

    @Override
    public void onSplashAdClose(CSJSplashAd csjSplashAd, int i) {
        callSplashAdClosed();
    }

    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }

    private DisplayMetrics getRealMetrics(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            @SuppressWarnings("rawtypes")
            Class c;
            try {
                c = Class.forName("android.view.Display");
                @SuppressWarnings("unchecked")
                Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, dm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dm;
    }

}
