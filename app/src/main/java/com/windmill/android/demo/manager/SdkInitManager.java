package com.windmill.android.demo.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.location.Location;

import com.windmill.sdk.WMAdConfig;
import com.windmill.sdk.WMCustomController;
import com.windmill.sdk.WindMillAd;

import java.util.List;

public class SdkInitManager {


    public static void initSDK(Context context,WindMillAd.WindMillAdInitListener listener) {

        WindMillAd ads = WindMillAd.sharedAds();
        ads.setDebugEnable(true);

        //https://doc.sigmob.com/tobid/21164/ SDK预置策略
        //针对首次安装后首次打开场景，设置SDK预置策略，并在版本集成时进行导入。
        //在项目的 src/main/assets 目录下新建一个目录 localStrategy，目录命名规则可以自定义，用于以下的步骤。
        //将从后台导出的pb文件放到刚新建的目录下 src/main/assets/localStrategy 。
//        ads.setLocalStrategyAssetPath(this, "localStrategy");

        //https://doc.sigmob.com/tobid/21160/ 第三方渠道SDK前置初始化
//        WMNetworkConfig.Builder builder = (new WMNetworkConfig.Builder())
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.ADMOB))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.REKLAMUP))
//                .addInitConfig(new WMAdnInitConfig(23, "appId"))
//                .addInitConfig(new WMAdnInitConfig(24, "appId"))
//                .addInitConfig(new WMAdnInitConfig(25, "appId"))
//                .addInitConfig(new WMAdnInitConfig(26, "appId"))//异步
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.VUNGLE, "appId"))//异步
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.UNITYADS, "appId"))//异步
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.IRONSOURCE, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.CSJ, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.GDT, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.KUAISHOU, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.KLEVIN, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.BAIDU, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.ADSCOPE, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.QUMENG, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.PANGLE, "appId"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.APPLOVIN, "appKey"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.APPLOVIN_MAX, "appKey"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.MOBVISTA, "appId", "appKey"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.SIGMOB, "appId", "appKey"))
//                .addInitConfig(new WMAdnInitConfig(WMNetworkConfig.TAPTAP, "appId", "appKey"));
//        ads.setInitNetworkConfig(builder.build());

        //https://doc.sigmob.com/tobid/21166/ 广告过滤
        /**
         * 下面构造了过滤表达式例子的含义为：
         *
         * 1、针对聚合广告位id:88888888 过滤gdt渠道下的广告位id:123
         * 2、针对聚合广告位id:88888888 过滤csj渠道下的广告位id:456
         * 3、针对聚合广告位id:88888888 过滤ks渠道下的广告位id集合:123、456、789
         * 4、针对聚合广告位id:88888888 过滤bd整个渠道
         * 5、针对聚合广告位id:88888888 过滤该瀑布流下的123、345、789等三方渠道的广告位id,与渠道无关
         * 6、针对聚合广告位id:88888888 过滤该瀑布流下的ks、csj、gdt等三方渠道
         * 7、针对聚合广告位id:88888888 过滤该瀑布流下的客户端bidding、服务端bidding、普通广告源（相当于过滤整个瀑布流）
         * 8、针对聚合广告位id:88888888 过滤该瀑布流下的客户端bidding的三方广告源
         * 9、针对聚合广告位id:88888888 过滤该瀑布流下的服务端bidding的三方广告源
         * 10、针对聚合广告位id:88888888 过滤该瀑布流下的非bidding的普通三方广告源
         * 11、针对聚合广告位id:88888888 过滤该瀑布流下价格在50-100之间的三方广告源
         * 12、针对聚合广告位id:88888888 过滤该瀑布流下价格小于等于50的三方广告源
         * 13、针对聚合广告位id:88888888 过滤该瀑布流下价格大于等于100的三方广告源
         * 14、针对聚合广告位id:88888888 过滤该瀑布流下价格等于50的三方广告源
         */
//        WindMillAd.sharedAds().addFilter(new WMWaterfallFilter("88888888")//针对这个聚合广告位进行的过滤
//                .equalTo(WMWaterfallFilter.KEY_CHANNEL_ID, "16")//gdt渠道id
//                .equalTo(WMWaterfallFilter.KEY_ADN_PLACEMENT_ID, "123")//渠道的广告位id
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMWaterfallFilter.KEY_CHANNEL_ID, "13")//csj渠道id
//                .and()//与的关系：不开启新的过滤表达式:可写可不写
//                .equalTo(WMWaterfallFilter.KEY_ADN_PLACEMENT_ID, "456")//渠道的广告位id
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMWaterfallFilter.KEY_CHANNEL_ID, "19")//快手渠道id
//                .in(WMWaterfallFilter.KEY_ADN_PLACEMENT_ID, Arrays.asList("123", "456", "789"))//渠道的广告位集合
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMWaterfallFilter.KEY_CHANNEL_ID, "21")//百度渠道id
//                .or()//或的关系：开启一个新的过滤表达式了
//                .in(WMWaterfallFilter.KEY_ADN_PLACEMENT_ID, Arrays.asList("123", "456", "789"))
//                .or()//或的关系：开启一个新的过滤表达式了
//                .in(WMWaterfallFilter.KEY_CHANNEL_ID, Arrays.asList("19", "13", "16"))
//                .or()//或的关系：开启一个新的过滤表达式了
//                .in(WMWaterfallFilter.KEY_BIDDING_TYPE, Arrays.asList(WMWaterfallFilter.C2S, WMWaterfallFilter.S2S, WMWaterfallFilter.NORMAL))
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMWaterfallFilter.KEY_BIDDING_TYPE, WMWaterfallFilter.C2S)
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMWaterfallFilter.KEY_BIDDING_TYPE, WMWaterfallFilter.S2S)
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMWaterfallFilter.KEY_BIDDING_TYPE, WMWaterfallFilter.NORMAL)
//                .or()//或的关系：开启一个新的过滤表达式了
//                .greaterThanEqual(WMWaterfallFilter.KEY_E_CPM, "50") //大于等于50
//                .lessThanEqual(WMWaterfallFilter.KEY_E_CPM, "100") //且小于等于100
//                .or()//或的关系：开启一个新的过滤表达式了
//                .lessThanEqual(WMWaterfallFilter.KEY_E_CPM, "50")
//                .or()//或的关系：开启一个新的过滤表达式了
//                .greaterThanEqual(WMWaterfallFilter.KEY_E_CPM, "100")
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMWaterfallFilter.KEY_E_CPM, "50")
//                .equalTo(WMWaterfallFilter.KEY_ADN_PLACEMENT_SUB_TYPE, "1")//过滤渠道子广告类型 （1激励 2开屏 4插屏 5原生 7banner）
//                .or()//或的关系：开启一个新的过滤表达式了
//                .equalTo(WMWaterfallFilter.KEY_ADN_PLACEMENT_RENDER_TYPE, "1")//过滤渠道子广告渲染类型（ 1使用原生自渲染 0使用对应广告类型渲染）
//        );

        // App的标准规则为全局设置，对全部Placement有效 https://doc.sigmob.com/tobid/21167/
//        Map<String, String> customMap = new HashMap<>();
//        customMap.put("user_source", "huawei");// 流量安装来源：oppo、华为等，开发者自己传
//        customMap.put("channel", "toutiao");// 买量渠道：穿山甲、快手、sigmob等
//        customMap.put("sub_channel", "toutiao");// 买量子渠道：穿山甲、快手、sigmob等
//        WindMillAd.sharedAds().initCustomMap(customMap);

        // Placement的自定义规则，只针对单个PlacementId有效
//        Map<String, String> customMap = new HashMap<>();
        //自定义参数的Key和Value
//        customMap.put("ToBid", "Test");
//        WindMillAd.sharedAds().initPlacementCustomMap("88888888", customMap);


        // 应用隐私控制设置 https://doc.sigmob.com/tobid/21161/
        PrivacySettingManager privacySettings = PrivacySettingManager.getInstance(context);
        ads.setAdult(privacySettings.isAdult());
        ads.setPersonalizedAdvertisingOn(privacySettings.isPersonalizedOn());
        try {
            ads.getClass().getMethod("setProgrammaticRecommendOn", boolean.class)
                    .invoke(ads, privacySettings.isProgrammaticRecommendOn());
        } catch (Exception e) {
            // SDK版本可能不支持此方法
        }

        ads.startWithAppId(context.getApplicationContext(), "16991", getWmAdConfig(privacySettings), listener);
    }

    /**
     * 自定义权限控制器，开发者可以通过实现WMCustomController接口，控制SDK获取用户设备信息的权限开关和获取用户设备信息的接口返回值。
     */
    private static WMAdConfig getWmAdConfig(PrivacySettingManager settings) {
        return new WMAdConfig.Builder().customController(new WMCustomController() {
            @Override
            public boolean isCanUseLocation() {
                return settings.canUseLocation();
            }

            @Override
            public Location getLocation() {
                return null;
            }

            @Override
            public boolean isCanUseAndroidId() {
                return settings.canUseAndroidId();
            }

            @Override
            public String getAndroidId() {
                return null;
            }

            @Override
            public String getDevOaid() {
                return null;
            }

            @Override
            public boolean isCanUseOaid() {
                return settings.canUseOaid();
            }

            @Override
            public boolean isCanUseAppList() {
                return settings.canUseAppList();
            }

            @Override
            public List<PackageInfo> getInstalledPackages() {
                return null;
            }

            @Override
            public boolean isCanUseWriteExternal() {
                return settings.canUseWriteExternal();
            }

            @Override
            public boolean isCanUsePermissionRecordAudio() {
                return settings.canUseRecordAudio();
            }

            @Override
            public boolean isCanUseSpaceSize() {
                return settings.canUseSpaceSize();
            }

            @Override
            public boolean isCanUseSensor() {
                return settings.canUseSensor();
            }

            @Override
            public boolean isCanUseSimOperator() {
                return settings.canUseSimOperator();
            }

            @Override
            public String getDevSimOperatorCode() {
                return null;
            }

            @Override
            public String getDevSimOperatorName() {
                return null;
            }
        }).build();
    }
}
