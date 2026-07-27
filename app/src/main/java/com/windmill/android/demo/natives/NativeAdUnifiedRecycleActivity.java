package com.windmill.android.demo.natives;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.windmill.android.demo.R;
import com.windmill.android.demo.view.ILoadMoreListener;
import com.windmill.android.demo.view.LoadMoreRecyclerView;
import com.windmill.android.demo.view.LoadMoreView;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.natives.WMNativeAd;
import com.windmill.sdk.natives.WMNativeAdData;
import com.windmill.sdk.natives.WMNativeAdLoadListener;
import com.windmill.sdk.natives.WMNativeAdRequest;

import java.util.ArrayList;
import java.util.List;


public class NativeAdUnifiedRecycleActivity extends Activity {

    private static final int LIST_ITEM_COUNT = 10;

    private LoadMoreRecyclerView mListView;

    private MyAdapter myAdapter;

    private WMNativeAd windNativeUnifiedAd;

    private int userID = 0;

    private String placementId;

    private List<WMNativeAdData> mData;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private int adWidth; // 广告宽高

    private static final int DEFAULT_LOAD_COUNT = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_unified_recycle);
        getExtraInfo();
        initListView();
        adWidth = screenWidthAsIntDips(this) - 20;//减20因为容器有个margin 10dp//340
    }

    private void getExtraInfo() {
        Intent intent = getIntent();
        placementId = intent.getStringExtra("placementId");
    }

    public static int screenWidthAsIntDips(Context context) {
        int pixels = context.getResources().getDisplayMetrics().widthPixels;
        float density = context.getResources().getDisplayMetrics().density;
        return (int) ((pixels / density) + 0.5f);
    }

    private void initListView() {
        mListView = (LoadMoreRecyclerView) findViewById(R.id.unified_native_ad_recycle);
        mListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mData = new ArrayList<>();
        myAdapter = new MyAdapter(this, mData);
        mListView.setAdapter(myAdapter);
        mListView.setLoadMoreListener(new ILoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadListAd();
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadListAd();
            }
        }, 500);
    }

    /**
     * 加载feed广告
     */
    private void loadListAd() {
        Log.d("lance", adWidth + "-----------loadListAd-----------" + placementId);
        userID++;
        if (windNativeUnifiedAd == null) {
            windNativeUnifiedAd = new WMNativeAd(this, new WMNativeAdRequest(placementId, String.valueOf(userID), null));
        }

        windNativeUnifiedAd.setNativeAdLoadListener(new WMNativeAdLoadListener() {
            @Override
            public void onError(WindMillError error, String placementId) {
                Log.d("lance", "onError:" + error.getMessage() + ":" + placementId);
                Toast.makeText(NativeAdUnifiedRecycleActivity.this, "onError:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }
            }

            @Override
            public void onFeedAdLoad(List<WMNativeAdData> unifiedADData, String placementId) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }

                if (unifiedADData != null && unifiedADData.size() > 0) {
                    Log.d("lance", "onFeedAdLoad:" + unifiedADData.size());
                    for (final WMNativeAdData adData : unifiedADData) {

                        for (int i = 0; i < LIST_ITEM_COUNT; i++) {
                            mData.add(null);
                        }

                        int count = mData.size();
                        mData.set(count - 1, adData);
                    }

                    myAdapter.notifyDataSetChanged();
                }
            }
        });
        windNativeUnifiedAd.loadAd(DEFAULT_LOAD_COUNT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mData != null) {
            for (WMNativeAdData ad : mData) {
                if (ad != null) {
                    ad.destroy();
                }
            }
        }
        mData = null;
    }

    private static class MyAdapter extends RecyclerView.Adapter {

        private static final int FOOTER_VIEW_COUNT = 1;

        private static final int ITEM_VIEW_TYPE_LOAD_MORE = -1;
        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_UNIFIED_AD = 1;
        private static final int ITEM_VIEW_TYPE_EXPRESS_AD = 2;

        private List<WMNativeAdData> mData;

        private Activity mActivity;

        public MyAdapter(Activity activity, List<WMNativeAdData> data) {
            this.mActivity = activity;
            this.mData = data;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ITEM_VIEW_TYPE_LOAD_MORE:
                    return new LoadMoreViewHolder(new LoadMoreView(mActivity));
                case ITEM_VIEW_TYPE_UNIFIED_AD:
                    return new UnifiedAdViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.listitem_ad_native, parent, false));
                case ITEM_VIEW_TYPE_EXPRESS_AD:
                    return new ExpressAdViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.listitem_ad_native, parent, false));
                default:
                    return new NormalViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.listitem_normal, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UnifiedAdViewHolder) {
                WMNativeAdData nativeAdData = mData.get(position);
                final UnifiedAdViewHolder adViewHolder = (UnifiedAdViewHolder) holder;
                NativeAdDemoRender nativeAdDemoRender = new NativeAdDemoRender();
                View adView = nativeAdDemoRender.getAdView(mActivity, nativeAdData);
                if (adViewHolder.adContainer != null) {
                    adViewHolder.adContainer.addView(adView);
                }

            } else if (holder instanceof ExpressAdViewHolder) {
                WMNativeAdData nativeAdData = mData.get(position);
                final ExpressAdViewHolder adViewHolder = (ExpressAdViewHolder) holder;
                nativeAdData.render();
                View expressAdView = nativeAdData.getExpressAdView();
                //添加进容器
                if (adViewHolder.adContainer != null) {
                    adViewHolder.adContainer.removeAllViews();
                    if (expressAdView != null) {
                        ViewGroup parent = (ViewGroup) expressAdView.getParent();
                        if (parent != null) {
                            parent.removeView(expressAdView);
                        }
                        adViewHolder.adContainer.addView(expressAdView);
                    }
                }
            } else if (holder instanceof NormalViewHolder) {
                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
                normalViewHolder.idle.setText("Recycler item " + position);
                holder.itemView.setBackgroundColor(getColorRandom());
            } else if (holder instanceof LoadMoreViewHolder) {
                LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
                loadMoreViewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        private int getColorRandom() {
            int a = Double.valueOf(Math.random() * 255).intValue();
            int r = Double.valueOf(Math.random() * 255).intValue();
            int g = Double.valueOf(Math.random() * 255).intValue();
            int b = Double.valueOf(Math.random() * 255).intValue();
            return Color.argb(a, r, g, b);
        }

        @Override
        public int getItemCount() {
            int count = mData == null ? 0 : mData.size();
            return count + FOOTER_VIEW_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            if (mData != null) {
                int count = mData.size();
                if (position >= count) {
                    return ITEM_VIEW_TYPE_LOAD_MORE;
                } else {
                    WMNativeAdData ad = mData.get(position);
                    if (ad == null) {
                        return ITEM_VIEW_TYPE_NORMAL;
                    } else {
                        if (ad.isExpressAd()) {
                            return ITEM_VIEW_TYPE_EXPRESS_AD;
                        } else {
                            return ITEM_VIEW_TYPE_UNIFIED_AD;
                        }

                    }
                }
            }
            return super.getItemViewType(position);
        }

        private static class ExpressAdViewHolder extends AdViewHolder {
            public ExpressAdViewHolder(View itemView) {
                super(itemView);
            }
        }

        private static class UnifiedAdViewHolder extends AdViewHolder {
            //媒体自渲染的View
            NativeAdDemoRender adRender;

            public UnifiedAdViewHolder(View itemView) {
                super(itemView);
                adRender = new NativeAdDemoRender();
            }
        }

        private static class AdViewHolder extends RecyclerView.ViewHolder {

            FrameLayout adContainer;

            public AdViewHolder(View itemView) {
                super(itemView);
                adContainer = (FrameLayout) itemView.findViewById(R.id.iv_list_item_container);
            }
        }

        private static class NormalViewHolder extends RecyclerView.ViewHolder {
            TextView idle;

            public NormalViewHolder(View itemView) {
                super(itemView);
                idle = (TextView) itemView.findViewById(R.id.text_idle);
            }
        }

        private static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;
            ProgressBar mProgressBar;

            public LoadMoreViewHolder(View itemView) {
                super(itemView);
                itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                mTextView = (TextView) itemView.findViewById(R.id.tv_load_more_tip);
                mProgressBar = (ProgressBar) itemView.findViewById(R.id.pb_load_more_progress);
            }
        }
    }
}