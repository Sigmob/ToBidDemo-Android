package com.windmill.android.demo.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdManager {

    // 广告类型常量
    public static final String AD_TYPE_REWARD = "激励";
    public static final String AD_TYPE_INTERSTITIAL = "插屏";
    public static final String AD_TYPE_NATIVE = "原生";
    public static final String AD_TYPE_BANNER = "横幅";
    public static final String AD_TYPE_SPLASH = "开屏";

    // 广告渠道列表
    public static final List<String> CHANNELS = new ArrayList<String>() {
        {
            add("SigMob");
            add("穿山甲");
            add("快手");
            add("优量汇");
            add("百度");
            add("ToBid ADX");
            add("AdScope");
            add("Tap ADN");
            add("趣盟");
            add("华为");
            add("荣耀");
            add("美数");
            add("章鱼");
        }
    };

    // 广告数据结构：渠道 -> 广告类型 -> 广告位列表
    private static final Map<String, Map<String, List<AdPlacement>>> adData = new HashMap<>();

    static {
        addSigMobAdPlacement();
        addCjsAdPlacement();
        addKuaiShouAdPlacement();
        addGdtAdPlacement();
        addBaiduAdPlacement();
        addToBidAdxAdPlacement();
        addAdScopeAdPlacement();
        addTapAdnAdPlacement();
        addQuMengAdPlacement();
        addHuaWeiAdPlacement();
        addHonorAdPlacement();
        addMeiShuAdPlacement();
        addZhangYuAdPlacement();
    }

    private static void addSigMobAdPlacement() {
        // 激励广告
        List<AdPlacement> rewardAds = createAdPlacements(
                "SigMob_激励视频", "9387595158051935"
        );

        // 插屏广告
        List<AdPlacement> interstitialAds = createAdPlacements(
                "Sigmob_新插屏_全屏", "8739513476824974",
                "Sigmob_新插屏_半屏", "2176248928423385",
                "Sigmob_全屏视频", "4753286031006593"
        );

        // 开屏广告
        List<AdPlacement> splashAds = createAdPlacements(
                "SigMob_开屏广告", "2009470615832232"
        );

        // 原生广告
        List<AdPlacement> nativeAds = createAdPlacements(
                "SigMob_原生广告", "9224761251541712"
        );

        // 添加SigMob渠道数据
        addChannelAdPlacement("SigMob", rewardAds, interstitialAds, splashAds, nativeAds, null);
    }

    private static void addCjsAdPlacement() {
        // 激励广告
        List<AdPlacement> rewardAds = createAdPlacements(
                "穿山甲_激励视频_模版渲染_横", "2737151569688265",
                "穿山甲_激励视频_模版渲染_竖", "4742225414998032",
                "穿山甲_激励视频_自渲染_竖", "8513782885671784"
        );

        // 插屏广告
        List<AdPlacement> interstitialAds = createAdPlacements(
                "穿山甲_插屏广告_新插屏_全屏_混合素材_竖", "9747535575453635",
                "穿山甲_插屏广告_新插屏_全屏_混合素材_横", "5589653696127381",
                "穿山甲_插屏广告_新插屏_全屏_图_竖", "5598968818689143",
                "穿山甲_插屏广告_新插屏_全屏_图_横", "5441713888857882",
                "穿山甲_插屏广告_新插屏_全屏_视频_竖", "3587278325529654",
                "穿山甲_插屏广告_新插屏_全屏_视频_横", "7252742468775386",
                "穿山甲_插屏广告_新插屏_半屏_混合素材_竖", "6734864931635222",
                "穿山甲_插屏广告_新插屏_半屏_混合素材_横", "8523994857147686",
                "穿山甲_插屏广告_新插屏_半屏_图_竖", "8858569375814668",
                "穿山甲_插屏广告_新插屏_半屏_图_横", "9528619564956395",
                "穿山甲_插屏广告_新插屏_半屏_视频_竖", "6689465581169203",
                "穿山甲_插屏广告_新插屏_半屏_视频_横", "1249272364839950"
        );

        // 开屏广告
        List<AdPlacement> splashAds = createAdPlacements(
                "穿山甲_开屏广告_自渲染_横", "3735463515583132",
                "穿山甲_开屏广告_自渲染_竖", "9313386614358887"
        );

        // 原生广告
        List<AdPlacement> nativeAds = createAdPlacements(
                "穿山甲_原生广告_Draw_模版渲染", "9243878171116818",
                "穿山甲_原生广告_模版渲染_混合素材_优选模版", "7214236327912625",
                "穿山甲_原生广告_模版渲染_图片_优选模版", "8983115712753298",
                "穿山甲_原生广告_模版渲染_视频_优选模版", "9766224373323983",
                "穿山甲_原生广告_自渲染_混合素材", "5771359946996735",
                "穿山甲_原生广告_自渲染_图片", "8762394945415572",
                "穿山甲_原生广告_自渲染_视频", "3169995143695363"
        );

        // 横幅广告
        List<AdPlacement> bannerAds = createAdPlacements(
                "穿山甲_横幅广告_模版渲染", "7697128192498681"
        );

        // 添加穿山甲渠道数据
        addChannelAdPlacement("穿山甲", rewardAds, interstitialAds, splashAds, nativeAds, bannerAds);
    }

    private static void addKuaiShouAdPlacement() {
        // 激励广告
        List<AdPlacement> rewardAds = createAdPlacements(
                "快手广告_激励视频_竖", "7115931754238704",
                "快手广告_激励视频_横", "3578842147646562"
        );

        // 插屏广告
        List<AdPlacement> interstitialAds = createAdPlacements(
                "快手广告_插屏广告_全屏视频_竖", "8299993567515832",
                "快手广告_插屏广告_全屏视频_横", "6431771728676629",
                "快手广告_插屏广告_模版渲染_竖_混合素材", "7734341669262440",
                "快手广告_插屏广告_模版渲染_竖_视频", "8423875343324245",
                "快手广告_插屏广告_模版渲染_竖_图", "5566547536353035",
                "快手广告_插屏广告_模版渲染_横_混合素材", "7168271443874827",
                "快手广告_插屏广告_模版渲染_横_视频", "9948268656975641",
                "快手广告_插屏广告_模版渲染_横_图", "1851616859925431"
        );

        // 开屏广告
        List<AdPlacement> splashAds = createAdPlacements(
                "快手广告_开屏广告_模版渲染", "5179213617529365"
        );

        // 原生广告
        List<AdPlacement> nativeAds = createAdPlacements(
                "快手广告_原生广告_Draw", "3969166469641223",
                "快手广告_原生广告_信息流_模版渲染_混合素材", "2691614432498176",
                "快手广告_原生广告_信息流_模版渲染_视频", "1786973122784990",
                "快手广告_原生广告_信息流_模版渲染_图", "7977224773598807",
                "快手广告_原生广告_信息流_自渲染_混合素材_横", "7871634357816606",
                "快手广告_原生广告_信息流_自渲染_视频_横", "2792286773817422",
                "快手广告_原生广告_信息流_自渲染_图_横", "4413248743488239",
                "快手广告_原生广告_信息流_自渲染_混合素材_竖", "7264823485628063",
                "快手广告_原生广告_信息流_自渲染_视频_竖", "1567671455752900",
                "快手广告_原生广告_信息流_自渲染_图_竖", "2953797599858748"
        );


        // 添加快手渠道数据
        addChannelAdPlacement("快手", rewardAds, interstitialAds, splashAds, nativeAds, null);
    }

    private static void addGdtAdPlacement() {
        // 激励广告
        List<AdPlacement> rewardAds = createAdPlacements(
                "腾讯广告_激励视频_横竖兼容", "5158581793348183",
                "腾讯广告_激励视频_竖版", "1282983658456008",
                "腾讯广告_激励视频_激励浏览", "4185669918397894"
        );

        // 插屏广告
        List<AdPlacement> interstitialAds = createAdPlacements(
                "腾讯广告_插屏广告_视频_竖版", "3874111189491671",
                "腾讯广告_插屏广告_视频_横版", "2862684688565436",
                "腾讯广告_插屏广告_优量汇渲染_混合素材_大小规格", "7819638175785186"
        );

        // 开屏广告
        List<AdPlacement> splashAds = createAdPlacements(
                "腾讯广告_开屏广告_视频", "6176589912132655",
                "腾讯广告_开屏广告_竖图", "1444173223421106",
                "腾讯广告_开屏广告_拼接竖图", "7817129553462891",
                "腾讯广告_开屏广告_V+", "5515679199113422"
        );

        // 原生广告
        List<AdPlacement> nativeAds = createAdPlacements(
                "腾讯广告_原生广告_优量汇渲染_混合素材_上图下文", "3175536281468891",
                "腾讯广告_原生广告_优量汇渲染_图文_上下图文", "5252713737373603",
                "腾讯广告_原生广告_优量汇渲染_图文_双图双文", "7957599425177346",
                "腾讯广告_原生广告_优量汇渲染_图文_左图右文", "5735897496237117",
                "腾讯广告_原生广告_优量汇渲染_图文_左文右图", "4522819844427977",
                "腾讯广告_原生广告_优量汇渲染_图文_三小图", "9867612898184787",
                "腾讯广告_原生广告_优量汇渲染_图文_文字浮层_上下图文", "4591435549199517",
                "腾讯广告_原生广告_优量汇渲染_图文_文字浮层_上图下文", "5888553534779278",
                "腾讯广告_原生广告_优量汇渲染_图文_文字浮层_单图单文", "5379437357531058",
                "腾讯广告_原生广告_优量汇渲染_图文_纯图1280*720", "6916437133994833",
                "腾讯广告_原生广告_优量汇渲染_图文_纯图1080x1920或800x1200", "9396874951762621",
                "腾讯广告_原生广告_优量汇渲染_视频_上下图文", "5286854415315391",
                "腾讯广告_原生广告_优量汇渲染_视频_双图双文", "5825726873931473",
                "腾讯广告_原生广告_优量汇渲染_视频_纯图1280*720", "8551249783415248",
                "腾讯广告_原生广告_自渲染_混合素材_1280*720(默认)", "4179171937516006",
                "腾讯广告_原生广告_自渲染_只出横版视频", "2759413823673782",
                "腾讯广告_原生广告_自渲染_只出竖版视频", "3276557729778549",
                "腾讯广告_原生广告_自渲染_混合素材_三小图或(1280*720)", "4169949986545330",
                "腾讯广告_原生广告_自渲染_图文_1080*1920或800*1200", "8243116323493090"
        );

        // 添加优量汇渠道数据
        addChannelAdPlacement("优量汇", rewardAds, interstitialAds, splashAds, nativeAds, null);
    }

    private static void addBaiduAdPlacement() {
        // 激励广告
        List<AdPlacement> rewardAds = createAdPlacements(
                "百度联盟_激励视频", "3816398934274348"
        );

        // 插屏广告
        List<AdPlacement> interstitialAds = createAdPlacements(
                "百度联盟_插屏广告_图片", "4643761825666972",
                "百度联盟_插屏广告_视频", "2652851931352738",
                "百度联盟_插屏广告_全屏视频", "2951495787812547"
        );

        // 开屏广告
        List<AdPlacement> splashAds = createAdPlacements(
                "百度联盟_开屏广告", "8917428365932184"
        );

        // 原生广告
        List<AdPlacement> nativeAds = createAdPlacements(
                "百度联盟_原生广告_模版渲染_信息流智能优选", "6728444626644345",
                "百度联盟_原生广告_自渲染_大图", "2294672163763161",
                "百度联盟_原生广告_自渲染_三图", "1273131329722038",
                "百度联盟_原生广告_自渲染_视频", "7537155742212838",
                "百度联盟_原生广告_自渲染_智能优选", "4356144419563636"
        );

        // 横幅广告
        List<AdPlacement> bannerAds = createAdPlacements(
                "百度联盟_横幅广告_20:3", "7464678695349420",
                "百度联盟_横幅广告_7:3", "9425359578357217",
                "百度联盟_横幅广告_3:2", "7896898276662026",
                "百度联盟_横幅广告_2:1", "1144613361889830"
        );

        // 添加百度渠道数据
        addChannelAdPlacement("百度", rewardAds, interstitialAds, splashAds, nativeAds, bannerAds);
    }

    private static void addToBidAdxAdPlacement() {
        // 开屏广告
        List<AdPlacement> splashAds = createAdPlacements(
                "ToBid_ADX_开屏广告", "6292247392518538"
        );

        // 原生广告
        List<AdPlacement> nativeAds = createAdPlacements(
                "ToBid_ADX_原生广告", "1916825915713921"
        );

        // 添加ToBid ADX渠道数据
        addChannelAdPlacement("ToBid ADX", null, null, splashAds, nativeAds, null);
    }

    private static void addAdScopeAdPlacement() {
        // 插屏广告
        List<AdPlacement> interstitialAds = createAdPlacements(
                "AdScope_插屏广告", "4687679666482024"
        );

        // 开屏广告
        List<AdPlacement> splashAds = createAdPlacements(
                "AdScope_开屏广告", "3638729744885982"
        );

        // 原生广告
        List<AdPlacement> nativeAds = createAdPlacements(
                "AdScope_原生广告_模版渲染_小图", "8935629976712759",
                "AdScope_原生广告_模版渲染_大图", "4542526289644256"
        );

        // 添加AdScope渠道数据
        addChannelAdPlacement("AdScope", null, interstitialAds, splashAds, nativeAds, null);
    }

    private static void addTapAdnAdPlacement() {
        // 激励广告
        List<AdPlacement> rewardAds = createAdPlacements(
                "TapADN_激励视频_模版渲染", "2148946584779368"
        );

        // 插屏广告
        List<AdPlacement> interstitialAds = createAdPlacements(
                "TapADN_插屏广告_模版渲染", "3213996581892968"
        );

        // 开屏广告
        List<AdPlacement> splashAds = createAdPlacements(
                "TapADN_开屏广告_模版渲染", "6711484376653465"
        );

        // 原生广告
        List<AdPlacement> nativeAds = createAdPlacements(
                "TapADN_原生广告_自渲染_视频", "9174259568999767",
                "TapADN_原生广告_自渲染_图文视频", "4462834318853273"
        );

        // 横幅广告
        List<AdPlacement> bannerAds = createAdPlacements(
                "TapADN_横幅广告_模版渲染", "4929885949871825"
        );

        // 添加Tap ADN渠道数据
        addChannelAdPlacement("Tap ADN", rewardAds, interstitialAds, splashAds, nativeAds, bannerAds);
    }

    private static void addQuMengAdPlacement() {
        // 激励广告
        List<AdPlacement> rewardAds = createAdPlacements(
                "趣盟_激励视频_模版渲染", "4739398723834228"
        );

        // 插屏广告
        List<AdPlacement> interstitialAds = createAdPlacements(
                "趣盟_插屏广告_模版渲染", "6742564842736006"
        );

        // 开屏广告
        List<AdPlacement> splashAds = createAdPlacements(
                "趣盟_开屏广告_模版渲染", "2428896127841773"
        );

        // 原生广告
        List<AdPlacement> nativeAds = createAdPlacements(
                "趣盟_原生广告_自渲染_大图", "9556113173878925",
                "趣盟_原生广告_自渲染_小图", "6161341797579594",
                "趣盟_原生广告_自渲染_竖版视频", "9993758134188439",
                "趣盟_原生广告_自渲染_横版视频", "4557452946714347"
        );

        // 添加趣盟渠道数据
        addChannelAdPlacement("趣盟", rewardAds, interstitialAds, splashAds, nativeAds, null);
    }

    private static void addHuaWeiAdPlacement() {
        // 激励广告
        List<AdPlacement> rewardAds = createAdPlacements(
                "HUAWEI_激励视频_640*360_16:9", "9742938789161474"
        );

        // 插屏广告
        List<AdPlacement> interstitialAds = createAdPlacements(
                "HUAWEI_插屏_图片_1080*1620", "7285721366643055",
                "HUAWEI_插屏_视频_720*1080", "2787473863473442",
                "HUAWEI_插屏_图片_720*1280", "9436144224352828",
                "HUAWEI_插屏_图片_1280*720", "7372695422771205"
        );

        // 开屏广告
        List<AdPlacement> splashAds = createAdPlacements(
                "HUAWEI_开屏_纯图_1080*1920_9:16", "9733484958934916",
                "HUAWEI_开屏_纯图_1080*1920_2:3", "8271394437764298",
                "HUAWEI_开屏_视频_720*1280_9:16", "6295535779584675"
        );

        // 原生广告
        List<AdPlacement> nativeAds = createAdPlacements(
                "HUAWEI_原生_大图文_1080*607_16:9", "9798737545245046",
                "HUAWEI_原生_小图文_160*160_1:1", "8753839997282431",
                "HUAWEI_原生_小图文_225*150_3:2", "4475465917858814",
                "HUAWEI_原生_组图文_225*150_3:2", "4898574656515220",
                "HUAWEI_原生_视频_640*360_16:9", "8886212735168597"
        );

        // 横幅广告
        List<AdPlacement> bannerAds = createAdPlacements(
                "HUAWEI_横幅_纯图_1080*170_108:17", "8548266629265021",
                "HUAWEI_横幅_纯图_1080*432_135:54", "4353915348695402"
        );

        // 添加HUAWEI渠道数据
        addChannelAdPlacement("HUAWEI", rewardAds, interstitialAds, splashAds, nativeAds, bannerAds);
    }

    private static void addHonorAdPlacement() {
        // 激励广告
        List<AdPlacement> rewardAds = createAdPlacements(
                "荣耀_激励视频_模版渲染", "1467433168682003"
        );

        // 插屏广告
        List<AdPlacement> interstitialAds = createAdPlacements(
                "荣耀_插屏广告", "1838688954732396"
        );

        // 开屏广告
        List<AdPlacement> splashAds = createAdPlacements(
                "荣耀_开屏广告", "9481612636991620"
        );

        // 原生广告
        List<AdPlacement> nativeAds = createAdPlacements(
                "荣耀_原生广告_自渲染", "5826722687592795",
                "荣耀_原生广告_模版", "6828256886195186"
        );

        // 横幅广告
        List<AdPlacement> bannerAds = createAdPlacements(
                "荣耀_横幅广告", "1872614968736582"
        );

        // 添加荣耀渠道数据
        addChannelAdPlacement("荣耀", rewardAds, interstitialAds, splashAds, nativeAds, bannerAds);
    }

    private static void addMeiShuAdPlacement() {
        // 激励广告
        List<AdPlacement> rewardAds = createAdPlacements(
                "美数_激励视频", "7342821987563575"
        );

        // 插屏广告
        List<AdPlacement> interstitialAds = createAdPlacements(
                "美数_插屏广告", "4627662676668983"
        );

        // 开屏广告
        List<AdPlacement> splashAds = createAdPlacements(
                "美数_开屏广告", "7442941677732000"
        );

        // 原生广告
        List<AdPlacement> nativeAds = createAdPlacements(
                "美数_原生广告_自渲染", "2387548611987822",
                "美数_原生广告_模版", "6441474723985228"
        );

        // 横幅广告
        List<AdPlacement> bannerAds = createAdPlacements(
                "美数_Banner", "6158355734968400"
        );

        // 添加美数渠道数据
        addChannelAdPlacement("美数", rewardAds, interstitialAds, splashAds, nativeAds, bannerAds);
    }

    private static void addZhangYuAdPlacement() {
        // 激励广告
        List<AdPlacement> rewardAds = createAdPlacements(
                "章鱼_激励视频_模版渲染", "7757624968263160"
        );

        // 插屏广告
        List<AdPlacement> interstitialAds = createAdPlacements(
                "章鱼_插屏广告_模版渲染", "4777735629222558"
        );

        // 开屏广告
        List<AdPlacement> splashAds = createAdPlacements(
                "章鱼_开屏广告_模版渲染", "7332172671985764"
        );

        // 原生广告
        List<AdPlacement> nativeAds = createAdPlacements(
                "章鱼_原生广告_自渲染", "1525253251761270",
                "章鱼_原生广告_模版", "2967137681722835"
        );

        // 横幅广告
        List<AdPlacement> bannerAds = createAdPlacements(
                "章鱼_Banner", "6661424956772228"
        );

        // 添加章鱼渠道数据
        addChannelAdPlacement("章鱼", rewardAds, interstitialAds, splashAds, nativeAds, bannerAds);
    }

    /**
     * 根据渠道和广告类型获取广告位列表
     *
     * @param channel 渠道名称
     * @param adType  广告类型
     * @return 广告位列表
     */
    public static List<AdPlacement> getAdPlacements(String channel, String adType) {
        Map<String, List<AdPlacement>> channelAds = adData.get(channel);
        if (channelAds != null) {
            List<AdPlacement> placements = channelAds.get(adType);
            if (placements != null) {
                return placements;
            }
        }
        return new ArrayList<>();
    }

    /**
     * 根据渠道、广告类型和位置获取广告位
     *
     * @param channel  渠道名称
     * @param adType   广告类型
     * @param position 位置索引
     * @return 广告位对象
     */
    public static AdPlacement getAdPlacement(String channel, String adType, int position) {
        List<AdPlacement> placements = getAdPlacements(channel, adType);
        if (position >= 0 && position < placements.size()) {
            return placements.get(position);
        }
        return null;
    }

    /**
     * 添加渠道广告数据
     * @param channel 渠道名称
     * @param rewardAds 激励广告位列表
     * @param interstitialAds 插屏广告位列表
     * @param splashAds 开屏广告位列表
     * @param nativeAds 原生广告位列表
     * @param bannerAds 横幅广告位列表
     */
    private static void addChannelAdPlacement(String channel,
                                              List<AdPlacement> rewardAds,
                                              List<AdPlacement> interstitialAds,
                                              List<AdPlacement> splashAds,
                                              List<AdPlacement> nativeAds,
                                              List<AdPlacement> bannerAds) {
        Map<String, List<AdPlacement>> ads = new HashMap<>();

        if (rewardAds != null && !rewardAds.isEmpty()) {
            ads.put(AD_TYPE_REWARD, rewardAds);
        }

        if (interstitialAds != null && !interstitialAds.isEmpty()) {
            ads.put(AD_TYPE_INTERSTITIAL, interstitialAds);
        }

        if (splashAds != null && !splashAds.isEmpty()) {
            ads.put(AD_TYPE_SPLASH, splashAds);
        }

        if (nativeAds != null && !nativeAds.isEmpty()) {
            ads.put(AD_TYPE_NATIVE, nativeAds);
        }

        if (bannerAds != null && !bannerAds.isEmpty()) {
            ads.put(AD_TYPE_BANNER, bannerAds);
        }

        if (!ads.isEmpty()) {
            adData.put(channel, ads);
        }
    }

    /**
     * 创建广告位列表
     * @param placements 广告位数组，格式为 [name1, id1, name2, id2, ...]
     * @return 广告位列表
     */
    private static List<AdPlacement> createAdPlacements(String... placements) {
        List<AdPlacement> result = new ArrayList<>();
        if (placements != null && placements.length > 0) {
            for (int i = 0; i < placements.length; i += 2) {
                if (i + 1 < placements.length) {
                    result.add(new AdPlacement(placements[i], placements[i + 1]));
                }
            }
        }
        return result;
    }

    /**
     * 广告位类
     */
    public static class AdPlacement {
        private String name;
        private String id;

        public AdPlacement(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}