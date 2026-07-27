package com.windmill.android.demo.natives;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.windmill.android.demo.R;
import com.windmill.android.demo.utils.UIUtils;
import com.windmill.android.demo.widget.OnViewPagerListener;
import com.windmill.android.demo.widget.ViewPagerLayoutManager;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.natives.WMNativeAd;
import com.windmill.sdk.natives.WMNativeAdData;
import com.windmill.sdk.natives.WMNativeAdLoadListener;
import com.windmill.sdk.natives.WMNativeAdRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class NativeAdDrawActivity extends Activity {
    private static final String TAG = "NativeAdDrawActivity";
    private String placementId;
    private RecyclerView mRecyclerView;
    private ViewPagerLayoutManager mLayoutManager;
    private DrawRecyclerAdapter mRecyclerAdapter;
    private List<TestItem> mDrawList = new ArrayList<>();
    private int adWidth, adHeight;
    private int[] images = {R.mipmap.video11, R.mipmap.video12, R.mipmap.video13, R.mipmap.video14, R.mipmap.video_2};
    private int[] videos = {R.raw.video11, R.raw.video12, R.raw.video13, R.raw.video14, R.raw.video_2};
    private WMNativeAd nativeUnifiedAd;

    private static final int DEFAULT_LOAD_COUNT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_draw);
        getExtraInfo();
        initView();
        initListener();
        initDefaultDate();
        loadDrawAd();
    }

    private void getExtraInfo() {
        Intent intent = getIntent();
        placementId = intent.getStringExtra("placementId");
        adWidth = (int) UIUtils.getScreenWidthDp(this);
        adHeight = (int) UIUtils.getHeight(this);
        Log.d("lance", adWidth + "---------screenWidthAsIntDips---------" + adHeight);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLayoutManager != null) {
            mLayoutManager.setOnViewPagerListener(null);
        }
    }

    private void initDefaultDate() {
        for (int i = 0; i < 5; i++) {
            TestItem.NormalVideo normalVideo = new TestItem.NormalVideo(videos[i], images[i]);
            mDrawList.add(new TestItem(normalVideo, null));
        }
        mRecyclerAdapter.notifyDataSetChanged();
    }

    /**
     * 加载Draw广告
     */
    private void loadDrawAd() {
        Log.d("lance", "-----------loadDrawAd-----------");
        if (nativeUnifiedAd == null) {
            nativeUnifiedAd = new WMNativeAd(this, new WMNativeAdRequest(placementId, String.valueOf(0), null));
        }

        nativeUnifiedAd.setNativeAdLoadListener(new WMNativeAdLoadListener() {
            @Override
            public void onError(WindMillError error, String placementId) {
                Log.d("lance", "----------onError----------:" + error.toString() + ":" + placementId);
                Toast.makeText(NativeAdDrawActivity.this, "onError:" + error.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFeedAdLoad(List<WMNativeAdData> unifiedADData, String placementId) {
                Log.d("lance", "----------onFeedAdLoad----------:" + (unifiedADData != null ? unifiedADData.size() : 0) + ":" + placementId);
                if (unifiedADData != null && unifiedADData.size() > 0) {
                    for (final WMNativeAdData adData : unifiedADData) {
                        Log.d("lance", unifiedADData.size() + "----------onFeedAdLoad----------:" + adData.isNativeDrawAd());
                        int random = (int) (Math.random() * 100);
                        int index = random % mDrawList.size();
                        if (index == 0) {
                            index++;
                        }
                        mDrawList.add(index, new TestItem(null, adData));
                    }

                    mRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
        nativeUnifiedAd.loadAd(DEFAULT_LOAD_COUNT);
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL, false);
        mRecyclerAdapter = new DrawRecyclerAdapter(this, mDrawList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    private void initListener() {
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                Log.d(TAG, "初始化完成");
                if (!mDrawList.get(0).isAdVideoView()) {
                    playVideo();
                }
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.d(TAG, "释放位置:" + position + " 下一页:" + isNext);
                int index = isNext ? 0 : 1;
                if (!mDrawList.get(position).isAdVideoView()) {
                    releaseVideo(index);
                }
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                Log.d(TAG, "选中位置:" + position + "  是否是滑动到底部:" + isBottom);
                if (!mDrawList.get(position).isAdVideoView()) {
                    playVideo();
                }
            }
        });
    }

    private void playVideo() {
        View itemView = mRecyclerView.getChildAt(0);
        if (itemView != null) {
            VideoView videoView = itemView.findViewById(R.id.video_view);
            final ImageView imgThumb = itemView.findViewById(R.id.video_thumb);

            if (videoView == null) {
                return;
            }

            if (!videoView.isPlaying()) {
                videoView.start();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        imgThumb.animate().alpha(0).setDuration(200).start();
                        return false;
                    }
                });
            } else {
                imgThumb.animate().alpha(0).setDuration(200).start();
            }
        }
    }

    private void releaseVideo(int index) {
        View itemView = mRecyclerView.getChildAt(index);
        if (itemView != null) {
            VideoView videoView = itemView.findViewById(R.id.video_view);
            if (videoView == null) {
                return;
            }
            ImageView imgThumb = itemView.findViewById(R.id.video_thumb);
            videoView.stopPlayback();
            imgThumb.animate().alpha(1).start();
        }
    }

    private static class DrawRecyclerAdapter extends RecyclerView.Adapter {
        private Activity mContext;
        private List<TestItem> mDataList;

        DrawRecyclerAdapter(Activity context, List<TestItem> dataList) {
            this.mContext = context;
            this.mDataList = dataList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ItemViewType.ITEM_VIEW_TYPE_EXPRESS_AD:
                    return new ExpressAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.draw_draw_item_view, parent, false));
                case ItemViewType.ITEM_VIEW_TYPE_UNIFIED_AD:
                    return new UnifiedAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.draw_draw_item_view, parent, false));
                case ItemViewType.ITEM_VIEW_TYPE_NORMAL:
                default:
                    return new NormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.draw_normal_item_view, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            TestItem item = mDataList.get(position);
            if (item == null) {
                return;
            }
            if (viewHolder instanceof NormalViewHolder) {
                NormalViewHolder normalViewHolder = (NormalViewHolder) viewHolder;
                normalViewHolder.videoView.setVideoURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + item.normalVideo.videoId));
                normalViewHolder.videoThumb.setImageResource(item.normalVideo.imgId);
                Glide.with(mContext).load(R.drawable.header_icon)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(normalViewHolder.authorIcon);
                normalViewHolder.videoThumb.setVisibility(View.VISIBLE);

            } else if (viewHolder instanceof ExpressAdViewHolder) {
                ExpressAdViewHolder drawViewHolder = (ExpressAdViewHolder) viewHolder;
                item.nativeAdData.render();
                View expressAdView = item.nativeAdData.getExpressAdView();
                //添加进容器
                if (expressAdView != null) {
                    ViewGroup parent = (ViewGroup) expressAdView.getParent();
                    if (parent != null) {
                        parent.removeView(expressAdView);
                    }
                    drawViewHolder.adContainer.removeAllViews();
                    drawViewHolder.adContainer.addView(expressAdView);
                }
            } else if (viewHolder instanceof UnifiedAdViewHolder) {
                UnifiedAdViewHolder unifiedAdViewHolder = (UnifiedAdViewHolder) viewHolder;
                NativeAdDrawRender nativeAdDrawRender = new NativeAdDrawRender();
                View adView = nativeAdDrawRender.getAdView(mContext, item.nativeAdData);
                if (unifiedAdViewHolder.adContainer != null) {
                    unifiedAdViewHolder.adContainer.addView(adView);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        @Override
        public int getItemViewType(int position) {
            TestItem item = mDataList.get(position);
            if (item.isAdVideoView()) {
                if (item.nativeAdData.isExpressAd()) {
                    return ItemViewType.ITEM_VIEW_TYPE_EXPRESS_AD;
                } else {
                    return ItemViewType.ITEM_VIEW_TYPE_UNIFIED_AD;
                }
            } else {
                return ItemViewType.ITEM_VIEW_TYPE_NORMAL;
            }
        }

        @IntDef({ItemViewType.ITEM_VIEW_TYPE_NORMAL, ItemViewType.ITEM_VIEW_TYPE_EXPRESS_AD, ItemViewType.ITEM_VIEW_TYPE_UNIFIED_AD})
        @Retention(RetentionPolicy.SOURCE)
        @Target(ElementType.PARAMETER)
        @interface ItemViewType {
            int ITEM_VIEW_TYPE_NORMAL = 0;
            int ITEM_VIEW_TYPE_EXPRESS_AD = 1;
            int ITEM_VIEW_TYPE_UNIFIED_AD = 2;
        }
    }


    private static class ExpressAdViewHolder extends AdViewHolder {
        public ExpressAdViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class UnifiedAdViewHolder extends AdViewHolder {
        //媒体自渲染的View
        NativeAdDrawRender adRender;

        public UnifiedAdViewHolder(View itemView) {
            super(itemView);
            adRender = new NativeAdDrawRender();
        }
    }

    private static class AdViewHolder extends RecyclerView.ViewHolder {

        FrameLayout adContainer;

        public AdViewHolder(View itemView) {
            super(itemView);
            adContainer = (FrameLayout) itemView.findViewById(R.id.video_container);
        }
    }

    private static class NormalViewHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private ImageView videoThumb;
        private ImageView authorIcon;

        NormalViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.video_view);
            videoThumb = itemView.findViewById(R.id.video_thumb);
            authorIcon = itemView.findViewById(R.id.author_icon);
        }
    }

    private static class TestItem {
        private NormalVideo normalVideo;
        private WMNativeAdData nativeAdData;

        TestItem(NormalVideo normalVideo, WMNativeAdData nativeAdData) {
            this.normalVideo = normalVideo;
            this.nativeAdData = nativeAdData;
        }

        boolean isAdVideoView() {
            return nativeAdData != null;
        }

        private static class NormalVideo {
            public int videoId;
            public int imgId;

            NormalVideo(int videoId, int imgId) {
                this.videoId = videoId;
                this.imgId = imgId;
            }
        }
    }

}