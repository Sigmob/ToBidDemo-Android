package com.windmill.android.demo.natives;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import com.windmill.android.demo.R;
import com.windmill.android.demo.utils.UIUtils;
import com.windmill.android.demo.widget.OnViewPagerListener;
import com.windmill.android.demo.widget.ViewPagerLayoutManager;
import com.windmill.sdk.WMConstants;
import com.windmill.sdk.WindMillError;
import com.windmill.sdk.models.AdInfo;
import com.windmill.sdk.natives.WMNativeAd;
import com.windmill.sdk.natives.WMNativeAdContainer;
import com.windmill.sdk.natives.WMNativeAdData;
import com.windmill.sdk.natives.WMNativeAdDataType;
import com.windmill.sdk.natives.WMNativeAdRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdDrawActivity extends Activity {
    private static final String TAG = "NativeAdDrawActivity";
    private String userID = "0";
    private String placementId;
    private RecyclerView mRecyclerView;
    private ViewPagerLayoutManager mLayoutManager;
    private DrawRecyclerAdapter mRecyclerAdapter;
    private List<TestItem> mDrawList = new ArrayList<>();
    private int adWidth, adHeight;
    private int[] images = {R.mipmap.video11, R.mipmap.video12, R.mipmap.video13, R.mipmap.video14, R.mipmap.video_2};
    private int[] videos = {R.raw.video11, R.raw.video12, R.raw.video13, R.raw.video14, R.raw.video_2};
    private WMNativeAd nativeUnifiedAd;

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
        if (TextUtils.isEmpty(placementId)) {
            String[] stringArray = getResources().getStringArray(R.array.native_draw_id_value);
            placementId = stringArray[1];
        }
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
        Map<String, Object> options = new HashMap<>();
        options.put(WMConstants.AD_WIDTH, adWidth);//针对于模版广告有效、单位dp
        options.put(WMConstants.AD_HEIGHT, adHeight);//针对于模版广告有效、单位dp
        options.put("user_id", userID);
        if (nativeUnifiedAd == null) {
            nativeUnifiedAd = new WMNativeAd(this, new WMNativeAdRequest(placementId, userID, 3, options));
        }

        nativeUnifiedAd.loadAd(new WMNativeAd.NativeAdLoadListener() {
            @Override
            public void onError(WindMillError error, String placementId) {
                Log.d("lance", "----------onError----------:" + error.toString() + ":" + placementId);
                Toast.makeText(NativeAdDrawActivity.this, "onError:" + error.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFeedAdLoad(String placementId) {

                List<WMNativeAdData> unifiedADData = nativeUnifiedAd.getNativeADDataList();

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
                bindListener(item.nativeAdData, viewHolder);
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
                bindListener(item.nativeAdData, viewHolder);
                //将容器和view链接起来
                item.nativeAdData.connectAdToView(mContext, unifiedAdViewHolder.windContainer, unifiedAdViewHolder.adRender);
                //添加进容器
                if (unifiedAdViewHolder.windContainer != null) {
                    ViewGroup parent = (ViewGroup) unifiedAdViewHolder.windContainer.getParent();
                    if (parent != null) {
                        parent.removeView(unifiedAdViewHolder.windContainer);
                    }
                    unifiedAdViewHolder.adContainer.removeAllViews();
                    unifiedAdViewHolder.adContainer.addView(unifiedAdViewHolder.windContainer);
                }
            }
        }

        private void bindListener(WMNativeAdData nativeAdData, RecyclerView.ViewHolder adViewHolder) {
            //设置广告交互监听
            nativeAdData.setInteractionListener(new WMNativeAdData.NativeAdInteractionListener() {
                @Override
                public void onADExposed(AdInfo adInfo) {
                    Log.d("lance", "----------onADExposed----------");
                }

                @Override
                public void onADClicked(AdInfo adInfo) {
                    Log.d("lance", "----------onADClicked----------");
                }

                @Override
                public void onADRenderSuccess(AdInfo adInfo, View view, float width, float height) {
                    Log.d("lance", "----------onRenderSuccess----------:" + width + ":" + height);
                }

                @Override
                public void onADError(AdInfo adInfo, WindMillError error) {
                    Log.d("lance", "----------onADError----------:" + error.toString());
                }

            });

            //设置media监听
            if (nativeAdData.getAdPatternType() == WMNativeAdDataType.NATIVE_VIDEO_AD) {
                nativeAdData.setMediaListener(new WMNativeAdData.NativeADMediaListener() {
                    @Override
                    public void onVideoLoad() {
                        Log.d("lance", "----------onVideoLoad----------");
                    }

                    @Override
                    public void onVideoError(WindMillError error) {
                        Log.d("lance", "----------onVideoError----------:" + error.toString());
                    }

                    @Override
                    public void onVideoStart() {
                        Log.d("lance", "----------onVideoStart----------");
                    }

                    @Override
                    public void onVideoPause() {
                        Log.d("lance", "----------onVideoPause----------");
                    }

                    @Override
                    public void onVideoResume() {
                        Log.d("lance", "----------onVideoResume----------");
                    }

                    @Override
                    public void onVideoCompleted() {
                        Log.d("lance", "----------onVideoCompleted----------");
                    }
                });
            }

            if (nativeAdData.getInteractionType() == WMConstants.INTERACTION_TYPE_DOWNLOAD) {
                nativeAdData.setDownloadListener(new WMNativeAdData.AppDownloadListener() {
                    @Override
                    public void onIdle() {
                        Log.d("lance", "----------onIdle----------");
                        if (adViewHolder instanceof UnifiedAdViewHolder) {
                            ((UnifiedAdViewHolder) adViewHolder).adRender.updateAdAction("开始下载");
                        }
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("lance", "----------onADExposed----------");
                        if (adViewHolder instanceof UnifiedAdViewHolder) {
                            ((UnifiedAdViewHolder) adViewHolder).adRender.updateAdAction("下载中");
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("lance", "----------onDownloadActive----------");
                        if (adViewHolder instanceof UnifiedAdViewHolder) {
                            ((UnifiedAdViewHolder) adViewHolder).adRender.updateAdAction("下载暂停");
                        }
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("lance", "----------onDownloadFailed----------");
                        if (adViewHolder instanceof UnifiedAdViewHolder) {
                            ((UnifiedAdViewHolder) adViewHolder).adRender.updateAdAction("重新下载");
                        }
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Log.d("lance", "----------onDownloadFinished----------");
                        if (adViewHolder instanceof UnifiedAdViewHolder) {
                            ((UnifiedAdViewHolder) adViewHolder).adRender.updateAdAction("点击安装");
                        }
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Log.d("lance", "----------onInstalled----------");
                        if (adViewHolder instanceof UnifiedAdViewHolder) {
                            ((UnifiedAdViewHolder) adViewHolder).adRender.updateAdAction("点击打开");
                        }
                    }
                });
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
        //创建一个装整个自渲染广告的容器
        WMNativeAdContainer windContainer;
        //媒体自渲染的View
        NativeAdDrawRender adRender;

        public UnifiedAdViewHolder(View itemView) {
            super(itemView);
            windContainer = new WMNativeAdContainer(itemView.getContext());
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