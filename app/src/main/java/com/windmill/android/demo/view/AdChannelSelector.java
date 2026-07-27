package com.windmill.android.demo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.windmill.android.demo.R;
import com.windmill.android.demo.manager.AdManager;
import com.windmill.android.demo.manager.AdManager.AdPlacement;

import java.util.ArrayList;
import java.util.List;

/**
 * 广告渠道和广告位选择器
 * 包含两个Spinner，分别用于选择广告渠道和广告位
 * 根据选择的广告类型自动过滤渠道列表，只显示有对应广告位的渠道
 * 选择渠道后自动更新广告位列表，只显示该渠道下的广告位
 */
public class AdChannelSelector extends LinearLayout {

    private Spinner channelSpinner;
    private Spinner adPlaceSpinner;
    private ArrayAdapter<String> channelAdapter;
    private ArrayAdapter<String> adPlaceAdapter;
    private List<String> channelList = new ArrayList<>();
    private List<String> adPlaceNames = new ArrayList<>();
    private List<AdPlacement> adPlacements = new ArrayList<>();
    private String currentChannel;
    private String currentAdType;
    private OnAdPlacementSelectedListener listener;

    public interface OnAdPlacementSelectedListener {
        void onAdPlacementSelected(String channel, String placementId, String placementName);
    }

    public AdChannelSelector(Context context) {
        super(context);
        init(context);
    }

    public AdChannelSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AdChannelSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("MissingInflatedId")
    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_ad_channel_selector, this, true);
        channelSpinner = view.findViewById(R.id.id_channel_spinner);
        adPlaceSpinner = view.findViewById(R.id.id_ad_place_spinner);

        // 初始化渠道适配器
        channelAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, channelList);
        channelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        channelSpinner.setAdapter(channelAdapter);

        // 初始化广告位适配器
        adPlaceAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, adPlaceNames);
        adPlaceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adPlaceSpinner.setAdapter(adPlaceAdapter);

        // 设置渠道选择监听器
        channelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentChannel = channelList.get(position);
                updateAdPlacements();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 设置广告位选择监听器
        adPlaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < adPlacements.size()) {
                    AdPlacement placement = adPlacements.get(position);
                    if (listener != null) {
                        listener.onAdPlacementSelected(currentChannel, placement.getId(), placement.getName());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * 设置广告类型
     *
     * @param adType 广告类型
     */
    public void setAdType(String adType) {
        this.currentAdType = adType;
        // 刷新渠道列表
        updateChannelList();
    }

    /**
     * 设置支持的渠道列表，自动过滤掉没有对应广告类型广告位的渠道
     *
     * @param channels 渠道列表
     */
    private void setChannels(List<String> channels) {
        List<String> validChannels = new ArrayList<>();

        // 过滤出有对应广告类型广告位的渠道
        if (currentAdType != null) {
            for (String channel : channels) {
                List<AdPlacement> placements = AdManager.getAdPlacements(channel, currentAdType);
                if (!placements.isEmpty()) {
                    validChannels.add(channel);
                }
            }
        } else {
            // 如果广告类型未设置，使用所有渠道
            validChannels.addAll(channels);
        }

        this.channelList.clear();
        this.channelList.addAll(validChannels);
        channelAdapter.notifyDataSetChanged();

        // 默认选择第一个渠道
        if (!channelList.isEmpty()) {
            currentChannel = channelList.get(0);
            updateAdPlacements();
        }
    }

    /**
     * 获取所有渠道列表
     *
     * @return 渠道列表
     */
    public List<String> getAllChannels() {
        return AdManager.CHANNELS;
    }

    /**
     * 设置广告位选择监听器
     *
     * @param listener 监听器
     */
    public void setOnAdPlacementSelectedListener(OnAdPlacementSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * 更新渠道列表，只包含有对应广告类型广告位的渠道
     */
    private void updateChannelList() {
        if (currentAdType == null) {
            return;
        }

        List<String> validChannels = new ArrayList<>();
        // 遍历所有渠道，过滤出有对应广告类型广告位的渠道
        for (String channel : AdManager.CHANNELS) {
            List<AdPlacement> placements = AdManager.getAdPlacements(channel, currentAdType);
            if (!placements.isEmpty()) {
                validChannels.add(channel);
            }
        }

        // 设置过滤后的渠道列表
        setChannels(validChannels);
    }

    /**
     * 更新广告位列表
     */
    private void updateAdPlacements() {
        if (currentChannel != null && currentAdType != null) {
            adPlacements = AdManager.getAdPlacements(currentChannel, currentAdType);
            adPlaceNames.clear();
            for (AdPlacement placement : adPlacements) {
                adPlaceNames.add(placement.getName());
            }
            adPlaceAdapter.notifyDataSetChanged();

            // 默认选择第一个广告位
            if (!adPlacements.isEmpty()) {
                AdPlacement placement = adPlacements.get(0);
                if (listener != null) {
                    listener.onAdPlacementSelected(currentChannel, placement.getId(), placement.getName());
                }
            }
        }
    }

    /**
     * 获取当前选中的渠道
     *
     * @return 当前渠道
     */
    public String getCurrentChannel() {
        return currentChannel;
    }

    /**
     * 获取当前选中的广告位ID
     *
     * @return 广告位ID
     */
    public String getCurrentPlacementId() {
        if (!adPlacements.isEmpty() && adPlaceSpinner.getSelectedItemPosition() >= 0) {
            return adPlacements.get(adPlaceSpinner.getSelectedItemPosition()).getId();
        }
        return null;
    }

    /**
     * 获取当前选中的广告位名称
     *
     * @return 广告位名称
     */
    public String getCurrentPlacementName() {
        if (!adPlacements.isEmpty() && adPlaceSpinner.getSelectedItemPosition() >= 0) {
            return adPlacements.get(adPlaceSpinner.getSelectedItemPosition()).getName();
        }
        return null;
    }
}