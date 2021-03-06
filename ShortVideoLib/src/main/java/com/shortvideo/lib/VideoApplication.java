package com.shortvideo.lib;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.shortvideo.lib.common.http.AseUtils;
import com.shortvideo.lib.common.http.RetrofitFactory;
import com.shortvideo.lib.model.ConfigBean;
import com.shortvideo.lib.model.DataMgr;
import com.shortvideo.lib.utils.LogCatStrategy;
import com.shortvideo.lib.utils.SPUtils;
import com.shortvideo.lib.utils.ToastyUtils;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import net.mikaelzero.mojito.Mojito;
import net.mikaelzero.mojito.loader.glide.GlideImageLoader;
import net.mikaelzero.mojito.view.sketch.SketchImageLoadFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

public class VideoApplication extends Application {

    private static VideoApplication application;

    //获取配置
    public static String CONFIG_URL;
    //首页视频
    public static String HOME_VIDEO_URL;
    //广告展示
    public static String AD_SHOW_URL;
    //广告点击
    public static String AD_CLICK_URL;
    //点赞
    public static String GO_LIKE_URL;
    //取消点赞
    public static String CANCEL_LIKE_URL;
    //获取下载地址
    public static String DOWNLOAD_PATH_URL;
    //举报
    public static String REPORT_URL;
    //视频详情
    public static String VIDEO_DETAIL_URL;
    //获取融合APP配置
    public static String FUSION_URL;
    //记录切换APP
    public static String STATE_CHANGE_URL;

    /**
     *
     */
    private void initEncryptApiUrl() {
        CONFIG_URL = AseUtils.AseEncrypt(getPackageName() + "get_config");
        HOME_VIDEO_URL = AseUtils.AseEncrypt(getPackageName() + "video");
        AD_SHOW_URL = AseUtils.AseEncrypt(getPackageName() + "count_show");
        AD_CLICK_URL = AseUtils.AseEncrypt(getPackageName() + "count_click");
        GO_LIKE_URL = AseUtils.AseEncrypt(getPackageName() + "like");
        CANCEL_LIKE_URL = AseUtils.AseEncrypt(getPackageName() + "dislike");
        DOWNLOAD_PATH_URL = AseUtils.AseEncrypt(getPackageName() + "download");
        REPORT_URL = AseUtils.AseEncrypt(getPackageName() + "feedback");
        VIDEO_DETAIL_URL = AseUtils.AseEncrypt(getPackageName() + "view");
        FUSION_URL = AseUtils.AseEncrypt(getPackageName() + "change_config");
        STATE_CHANGE_URL = AseUtils.AseEncrypt(getPackageName() + "stat_change");
    }

    //ApiVersion
    public static String API_VERSION;

    //Firebase
    private FirebaseAnalytics mFirebaseAnalytics;

    private String waterPicPath = "";
    private long maxWatchTime = 1000_000_000_000_000L;
    private int maxWatchNumber = 1000;

    //续看的位置
    private int lastVideoPosition = 0;

    private boolean pageScoll = false;
    private boolean jumpPop = false;

    private String utm_source;
    private String utm_medium;
    private String utm_install_time;
    private String utm_version;

    /**
     * 自定义属性开始
     **/
    //是否是生产环境
    private boolean isProduct = false;
    //选择加载视频布局1(右侧按钮栏)，或布局2(左侧按钮栏)
    private int videoLayoutType = 1;
    //是否提供纯享功能
    private boolean isPureEnjoyment = false;
    //是否提供下载功能
    private boolean applyDownload = false;
    //是否提供点赞功能
    private boolean applyToLike = false;
    //视频App启动页面，1.新版首页  2.前置页  3.旧版短视频
    private int openPageWhere = 1;
    //前置页是否提供首页视频功能
    private boolean applyFrontHomeVideo = false;
    //前置页是否提供首页消息功能
    private boolean applyFrontHomeMessage = false;
    //前置页是否提供首页图库功能
    private boolean applyFrontHomePhotos = false;
    //前置页是否提供首页图片编辑功能
    private boolean applyFrontHomeEdit = false;
    //前置页是否提供首页我的功能
    private boolean applyFrontHomeMine = false;
    //前置页列表布局，1.两排格子  2.垂直布局
    private int frontListLayoutType = 1;
    //前置页列表是否展示点赞数
    private boolean applyFrontLikeNum = false;
    //前置页列表点赞数位置，1.左上  2.右下
    private int frontLikeNumLayout = 1;
    //前置页2排格子列表单个的高度(单位dp)
    private float frontListItemHeight = 165;
    //前置页背景颜色
    private int frontPageBgColor = R.color.black;
    //新版首页背景颜色
    private int ncIndexBgColor = R.color.white;
    //新版首页标题文字颜色
    private int ncIndexTitleColor = R.color.black;
    //前置页底部菜单栏背景颜色
    private int frontPageBottomBgColor = R.color.color_181818;
    //前置页是否展示标题(如果不展示，则下划线也不展示)
    private boolean applyFrontPageTitle = false;
    //前置页标题文字以及内容文字颜色(目前保持标题文字和内容文字颜色一致，防止与背景色互斥)
    private int frontPageTitleColor = R.color.white;
    //前置页标题文字大小
    private int frontPageTitleSize = 18;
    //前置页标题相对位置，1.居左  2.居中  3.居右
    private int frontPageTitleLayoutType = 2;
    //前置页是否展示标题下划线
    private boolean applyFrontPageIndicator = false;
    //前置页下划线相对标题位置，1.居左  2.居中  3.居右
    private int frontPageIndicatorLayoutType = 2;
    //前置页标题下划线颜色
    private int frontPageIndicatorColor = R.color.white;
    //前置页标题下划线长度(单位dp)
    private float frontPageIndicatorWidth = 20;
    //前置页标题下划线高度(单位dp)
    private float frontPageIndicatorHeight = 6;
    //前置页标题下划线圆角
    private float frontPageIndicatorCornersRadius = 3;
    //前置页是否提供拍照功能
    private boolean applyFrontPageTakeVideo = false;
    //前置页图库列表样式，1.列表  2.一屏多页
    private int frontPhotosLayoutType = 1;
    //前置页图库列表一屏多页滑动方式，1.左右  2.上下
    private int frontPhotosScollType = 1;
    //前置页图库列表的列数(2-4列)
    private int frontPhotosSpanCount = 2;
    //前置页图库是否展示点赞数
    private boolean applyFrontPhotosLikeNum = false;
    //前置页图库是否提供设置壁纸功能
    private boolean applyFrontPhotosWallpaper = false;

    /**
     * 自定义属性结束
     **/

    public static VideoApplication getInstance() {
        return application;
    }

    public FirebaseAnalytics getmFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

    public String getWaterPicPath() {
        return waterPicPath;
    }

    public void setWaterPicPath(String waterPicPath) {
        this.waterPicPath = waterPicPath;
    }

    public String getUtm_source() {
        return utm_source;
    }

    public String getUtm_medium() {
        return utm_medium;
    }

    public String getUtm_install_time() {
        return utm_install_time;
    }

    public String getUtm_version() {
        return utm_version;
    }

    public long getMaxWatchTime() {
        return maxWatchTime;
    }

    public void setMaxWatchTime(long maxWatchTime) {
        this.maxWatchTime = maxWatchTime;
    }

    public int getMaxWatchNumber() {
        return maxWatchNumber;
    }

    public void setMaxWatchNumber(int maxWatchNumber) {
        this.maxWatchNumber = maxWatchNumber;
    }

    public boolean isPageScoll() {
        return pageScoll;
    }

    public void setPageScoll(boolean pageScoll) {
        this.pageScoll = pageScoll;
    }

    public boolean isJumpPop() {
        return jumpPop;
    }

    public void setJumpPop(boolean jumpPop) {
        this.jumpPop = jumpPop;
    }

    public int getLastVideoPosition() {
        return lastVideoPosition;
    }

    public void setLastVideoPosition(int lastVideoPosition) {
        this.lastVideoPosition = lastVideoPosition;
    }


    /**
     * 自定义属性开始
     **/
    public VideoApplication setDefaultUrl(String url) {
        RetrofitFactory.NEW_URL = url;
        return application;
    }

    public VideoApplication setApiVersion(String apiVersion) {
        API_VERSION = apiVersion;
        return application;
    }

    public VideoApplication setProduct(boolean product) {
        isProduct = product;
        return application;
    }

    public VideoApplication setVideoLayoutType(int videoLayoutType) {
        this.videoLayoutType = videoLayoutType > 2 ? 2 : Math.max(videoLayoutType, 1);
        return application;
    }

    public VideoApplication setPureEnjoyment(boolean pureEnjoyment) {
        isPureEnjoyment = pureEnjoyment;
        return application;
    }

    public VideoApplication setApplyDownload(boolean applyDownload) {
        this.applyDownload = applyDownload;
        return application;
    }

    public VideoApplication setApplyToLike(boolean applyToLike) {
        this.applyToLike = applyToLike;
        return application;
    }

    public VideoApplication setOpenPageWhere(int openPageWhere) {
        this.openPageWhere = openPageWhere;
        return application;
    }

    public VideoApplication setApplyFrontHomeVideo(boolean applyFrontHomeVideo) {
        this.applyFrontHomeVideo = applyFrontHomeVideo;
        return application;
    }

    public VideoApplication setApplyFrontHomeMessage(boolean applyFrontHomeMessage) {
        this.applyFrontHomeMessage = applyFrontHomeMessage;
        return application;
    }

    public VideoApplication setApplyFrontHomePhotos(boolean applyFrontHomePhotos) {
        this.applyFrontHomePhotos = applyFrontHomePhotos;
        return application;
    }

    public VideoApplication setApplyFrontHomeEdit(boolean applyFrontHomeEdit) {
        this.applyFrontHomeEdit = applyFrontHomeEdit;
        return application;
    }

    public VideoApplication setApplyFrontHomeMine(boolean applyFrontHomeMine) {
        this.applyFrontHomeMine = applyFrontHomeMine;
        return application;
    }

    public VideoApplication setFrontListLayoutType(int frontListLayoutType) {
        this.frontListLayoutType = frontListLayoutType;
        return application;
    }

    public VideoApplication setApplyFrontLikeNum(boolean applyFrontLikeNum) {
        this.applyFrontLikeNum = applyFrontLikeNum;
        return application;
    }

    public VideoApplication setFrontLikeNumLayout(int frontLikeNumLayout) {
        this.frontLikeNumLayout = frontLikeNumLayout;
        return application;
    }

    public VideoApplication setFrontListItemHeight(float frontListItemHeight) {
        this.frontListItemHeight = frontListItemHeight;
        return application;
    }

    public VideoApplication setFrontPageBgColor(int frontPageBgColor) {
        this.frontPageBgColor = frontPageBgColor;
        return application;
    }

    public VideoApplication setNcIndexBgColor(int ncIndexBgColor) {
        this.ncIndexBgColor = ncIndexBgColor;
        return application;
    }

    public VideoApplication setNcIndexTitleColor(int ncIndexTitleColor) {
        this.ncIndexTitleColor = ncIndexTitleColor;
        return application;
    }

    public VideoApplication setFrontPageBottomBgColor(int frontPageBottomBgColor) {
        this.frontPageBottomBgColor = frontPageBottomBgColor;
        return application;
    }

    public VideoApplication setApplyFrontPageTitle(boolean applyFrontPageTitle) {
        this.applyFrontPageTitle = applyFrontPageTitle;
        return application;
    }

    public VideoApplication setFrontPageTitleColor(int frontPageTitleColor) {
        this.frontPageTitleColor = frontPageTitleColor;
        return application;
    }

    public VideoApplication setFrontPageTitleSize(int frontPageTitleSize) {
        this.frontPageTitleSize = frontPageTitleSize;
        return application;
    }

    public VideoApplication setFrontPageTitleLayoutType(int frontPageTitleLayoutType) {
        this.frontPageTitleLayoutType = frontPageTitleLayoutType;
        return application;
    }

    public VideoApplication setApplyFrontPageIndicator(boolean applyFrontPageIndicator) {
        this.applyFrontPageIndicator = applyFrontPageIndicator;
        return application;
    }

    public VideoApplication setFrontPageIndicatorLayoutType(int frontPageIndicatorLayoutType) {
        this.frontPageIndicatorLayoutType = frontPageIndicatorLayoutType;
        return application;
    }

    public VideoApplication setFrontPageIndicatorColor(int frontPageIndicatorColor) {
        this.frontPageIndicatorColor = frontPageIndicatorColor;
        return application;
    }

    public VideoApplication setFrontPageIndicatorWidth(float frontPageIndicatorWidth) {
        this.frontPageIndicatorWidth = frontPageIndicatorWidth;
        return application;
    }

    public VideoApplication setFrontPageIndicatorHeight(float frontPageIndicatorHeight) {
        this.frontPageIndicatorHeight = frontPageIndicatorHeight;
        return application;
    }

    public VideoApplication setFrontPageIndicatorCornersRadius(float frontPageIndicatorCornersRadius) {
        this.frontPageIndicatorCornersRadius = frontPageIndicatorCornersRadius;
        return application;
    }

    public VideoApplication setApplyFrontPageTakeVideo(boolean applyFrontPageTakeVideo) {
        this.applyFrontPageTakeVideo = applyFrontPageTakeVideo;
        return application;
    }

    public VideoApplication setFrontPhotosLayoutType(int frontPhotosLayoutType) {
        this.frontPhotosLayoutType = frontPhotosLayoutType;
        return application;
    }

    public VideoApplication setFrontPhotosScollType(int frontPhotosScollType) {
        this.frontPhotosScollType = frontPhotosScollType;
        return application;
    }

    public VideoApplication setFrontPhotosSpanCount(int frontPhotosSpanCount) {
        this.frontPhotosSpanCount = frontPhotosSpanCount;
        return application;
    }

    public VideoApplication setApplyFrontPhotosLikeNum(boolean applyFrontPhotosLikeNum) {
        this.applyFrontPhotosLikeNum = applyFrontPhotosLikeNum;
        return application;
    }

    public VideoApplication setApplyFrontPhotosWallpaper(boolean applyFrontPhotosWallpaper) {
        this.applyFrontPhotosWallpaper = applyFrontPhotosWallpaper;
        return application;
    }
    /** 自定义属性结束 **/

    /**
     * 获取自定义属性开始
     **/
    public boolean isProduct() {
        return isProduct;
    }

    public int getVideoLayoutType() {
        return videoLayoutType;
    }

    public boolean isPureEnjoyment() {
        return isPureEnjoyment;
    }

    public boolean isApplyDownload() {
        return applyDownload;
    }

    public boolean isApplyToLike() {
        return applyToLike;
    }

    public int getOpenPageWhere() {
        return openPageWhere;
    }

    public boolean isApplyFrontHomeVideo() {
        return applyFrontHomeVideo;
    }

    public boolean isApplyFrontHomeMessage() {
        return applyFrontHomeMessage;
    }

    public boolean isApplyFrontHomePhotos() {
        return applyFrontHomePhotos;
    }

    public boolean isApplyFrontHomeEdit() {
        return applyFrontHomeEdit;
    }

    public boolean isApplyFrontHomeMine() {
        return applyFrontHomeMine;
    }

    public int getFrontListLayoutType() {
        return frontListLayoutType;
    }

    public boolean isApplyFrontLikeNum() {
        return applyFrontLikeNum;
    }

    public int getFrontLikeNumLayout() {
        return frontLikeNumLayout;
    }

    public float getFrontListItemHeight() {
        return frontListItemHeight;
    }

    public int getFrontPageBgColor() {
        return frontPageBgColor;
    }

    public int getNcIndexBgColor() {
        return ncIndexBgColor;
    }

    public int getNcIndexTitleColor() {
        return ncIndexTitleColor;
    }

    public int getFrontPageBottomBgColor() {
        return frontPageBottomBgColor;
    }

    public boolean isApplyFrontPageTitle() {
        return applyFrontPageTitle;
    }

    public int getFrontPageTitleColor() {
        return frontPageTitleColor;
    }

    public int getFrontPageTitleSize() {
        return frontPageTitleSize;
    }

    public int getFrontPageTitleLayoutType() {
        return frontPageTitleLayoutType;
    }

    public boolean isApplyFrontPageIndicator() {
        return applyFrontPageIndicator;
    }

    public int getFrontPageIndicatorLayoutType() {
        return frontPageIndicatorLayoutType;
    }

    public int getFrontPageIndicatorColor() {
        return frontPageIndicatorColor;
    }

    public float getFrontPageIndicatorWidth() {
        return frontPageIndicatorWidth;
    }

    public float getFrontPageIndicatorHeight() {
        return frontPageIndicatorHeight;
    }

    public float getFrontPageIndicatorCornersRadius() {
        return frontPageIndicatorCornersRadius;
    }

    public boolean isApplyFrontPageTakeVideo() {
        return applyFrontPageTakeVideo;
    }

    public int getFrontPhotosLayoutType() {
        return frontPhotosLayoutType;
    }

    public int getFrontPhotosScollType() {
        return frontPhotosScollType;
    }

    public int getFrontPhotosSpanCount() {
        return frontPhotosSpanCount;
    }

    public boolean isApplyFrontPhotosLikeNum() {
        return applyFrontPhotosLikeNum;
    }

    public boolean isApplyFrontPhotosWallpaper() {
        return applyFrontPhotosWallpaper;
    }

    /**
     * 获取自定义属性结束
     **/

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        //初始化sp
        SPUtils.init(this);

        //log初始化
        initLog();

        //Toast初始化
        initToast();

        //连接Google pay分析来源
        connectGooglePlay();

        //初始化Google统计
        initFirebase();

        //初始化视频组件
        initGSYVideo();

        //开启视频处理LOG
        RxFFmpegInvoke.getInstance().setDebug(false);

        //初始化图片查看器
        initMojito();

        //初始化个人信息
        initUserConfig();

        //初始化自定义页面配置
        initVideoPageConfig();

        //初始化Fresco
        Fresco.initialize(this);

        AseUtils.init(this);
        //初始化加密API地址
        initEncryptApiUrl();
    }

    private void initUserConfig() {
        if (!TextUtils.isEmpty(SPUtils.getString("fusion_get_configs"))) {
            DataMgr.getInstance().setUser(new Gson().fromJson(SPUtils.getString("fusion_get_configs"), ConfigBean.class));
        } else {
            DataMgr.getInstance().getUser().setUdid(VideoApplication.getInstance().getUDID());
            DataMgr.getInstance().getUser().setSysInfo(VideoApplication.getInstance().getSysInfo());
        }
    }

    /**
     * 初始化自定义页面配置
     */
    protected void initVideoPageConfig() {
        setDefaultUrl("http://172.247.143.109:85/")               //默认域名
                .setProduct(false)                                //是否为生产环境
                .setApiVersion("1.0.0.1")                         //是否加密解密
                .setVideoLayoutType(1)                            //选择加载视频布局1(右侧按钮栏)，或布局2(左侧按钮栏)
                .setPureEnjoyment(true)                           //开启纯享功能
                .setApplyDownload(true)                           //提供下载功能
                .setApplyToLike(true)                             //提供点赞功能
                .setOpenPageWhere(3);                              //视频App启动页面，1.新版首页  2.前置页  3.旧版短视频
//                .setApplyFrontHomeVideo(false)                     //前置页是否提供首页视频功能
//                .setApplyFrontHomeMessage(false)                   //前置页是否提供首页消息功能
//                .setApplyFrontHomePhotos(false)                    //前置页是否提供首页图库功能
//                .setApplyFrontHomeEdit(false)                      //前置页是否提供首页图片编辑功能
//                .setApplyFrontHomeMine(false)                      //前置页是否提供首页我的功能
//                .setFrontListLayoutType(1)                        //前置页列表布局，1.两排格子  2.垂直布局
//                .setFrontListItemHeight(165)                      //前置页2排格子列表单个的高度
//                .setApplyFrontLikeNum(false)                       //前置页列表是否展示点赞数
//                .setFrontLikeNumLayout(1)                         //前置页列表点赞数位置，1.左上  2.右下
//                .setFrontPageBgColor(R.color.black)               //前置页背景颜色
//                .setNcIndexBgColor(R.color.white)                 //新版首页背景颜色
//                .setNcIndexTitleColor(R.color.black)              //新版首页标题文字颜色
//                .setFrontPageBottomBgColor(R.color.color_181818)  //前置页底部菜单栏背景颜色
//                .setApplyFrontPageTitle(true)                     //前置页是否展示标题(如果不展示，则下划线也不展示)
//                .setFrontPageTitleColor(R.color.white)            //前置页标题、内容文字颜色
//                .setFrontPageTitleSize(18)                        //前置页标题文字大小
//                .setFrontPageTitleLayoutType(2)                   //前置页标题相对位置，1.居左  2.居中  3.居右
//                .setApplyFrontPageIndicator(true)                 //前置页是否展示标题下划线
//                .setFrontPageIndicatorLayoutType(2)               //前置页下划线相对标题位置，1.居左  2.居中  3.居右
//                .setFrontPageIndicatorWidth(20)                   //前置页标题下划线宽度
//                .setFrontPageIndicatorHeight(3)                   //前置页标题下划线高度
//                .setFrontPageIndicatorColor(R.color.white)        //前置页标题下划线颜色
//                .setFrontPageIndicatorCornersRadius(6)            //前置页标题下划线圆角值
//                .setApplyFrontPageTakeVideo(true)                 //前置页是否提供拍摄功能
//                .setFrontPhotosLayoutType(1)                      //前置页图库列表样式，1.列表  2.一屏多页
//                .setFrontPhotosScollType(1)                       //前置页图库列表一屏多页滑动方式，1.左右  2.上下
//                .setFrontPhotosSpanCount(2)                       //前置页图库列表的列数
//                .setApplyFrontPhotosLikeNum(false)                //前置页图库是否展示点赞数
//                .setApplyFrontPhotosWallpaper(false);             //前置页图库是否提供设置壁纸功能
    }

    /**
     * log初始化
     */
    private void initLog() {
        PrettyFormatStrategy strategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .logStrategy(new LogCatStrategy())
                .methodCount(1)
                .tag("RESULT")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(strategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    /**
     * Toast初始化
     */
    private void initToast() {
        ToastyUtils.init(this);
    }

    /**
     * 初始化Firebase
     */
    private void initFirebase() {
        //开启Google分析
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        //启动次数
        SPUtils.set("app_open_cout", SPUtils.getInteger("app_open_cout") + 1);
    }

    /**
     * 谷歌自定义事件上报
     *
     * @param eventName
     * @param bundle
     */
    public void reportToGoogle(String eventName, Bundle bundle) {
        if (!TextUtils.isEmpty(eventName) && bundle != null)
            mFirebaseAnalytics.logEvent(eventName, bundle);
    }

    /**
     * 连接Google pay分析来源
     */
    private void connectGooglePlay() {
        if (TextUtils.isEmpty(SPUtils.getString("install_referrer_info"))) {
            InstallReferrerClient installReferrerClient = InstallReferrerClient.newBuilder(this).build();
            installReferrerClient.startConnection(new InstallReferrerStateListener() {
                @Override
                public void onInstallReferrerSetupFinished(int responseCode) {
                    switch (responseCode) {
                        case InstallReferrerClient.InstallReferrerResponse.OK:
                            // Connection established.
                            Logger.e("Google Play连接成功");

                            ReferrerDetails response;
                            try {
                                response = installReferrerClient.getInstallReferrer();
                                String referrerUrl = response.getInstallReferrer();
                                utm_install_time = response.getInstallBeginTimestampSeconds() + "";
                                utm_version = response.getInstallVersion();
                                if (!TextUtils.isEmpty(referrerUrl)) {
                                    utm_source = referrerUrl.substring(referrerUrl.indexOf("utm_source=") + 11, referrerUrl.indexOf("&"));
                                    utm_medium = referrerUrl.substring(referrerUrl.indexOf("utm_medium=") + 11);
                                    Logger.e("获取Google Play下载信息\n来源: " + utm_source + "\n渠道: " + utm_medium + "\n安装时间: " + utm_install_time + "\n版本号: " + utm_version);
                                    SPUtils.set("install_referrer_info", referrerUrl);
                                }
                                installReferrerClient.endConnection();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                installReferrerClient.endConnection();
                                Logger.e("Google Play下载信息获取异常: " + e.getMessage());
                            }

                            break;
                        case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                            // API not available on the current Play Store app.
                            Logger.e("Google Play API 在当前 Play 商店应用中不可用");
                            break;
                        case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                            // Connection couldn't be established.
                            Logger.e("Google Play无法建立连接");
                            break;
                    }
                }

                @Override
                public void onInstallReferrerServiceDisconnected() {
                    Logger.e("Google Play断开连接");
                }
            });
        }
    }

    /**
     * 初始化视频组件
     */
    private void initGSYVideo() {
        //EXOPlayer内核，支持格式更多
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        //exo缓存模式，支持m3u8，只支持exo
        CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
        //切换渲染模式
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
    }

    /**
     * 初始化图片查看器
     */
    private void initMojito() {
        Mojito.initialize(
                GlideImageLoader.Companion.with(this),
                new SketchImageLoadFactory()
        );
    }

    /**
     * 获取版本号名称
     *
     * @return
     */
    public String getVerName() {
        String verName = "";
        try {
            verName = getPackageManager().
                    getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 判断APP是否是Debug模式
     *
     * @return
     */
    public boolean isDebugMode() {
        return getApplicationInfo() != null
                && (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取手机的唯一标识
     *
     * @return
     */
    public String getUDID() {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        try {
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                deviceId.append("imei");
                deviceId.append(imei);
                return deviceId.toString();
            }
            //序列号（sn）
            @SuppressLint("MissingPermission") String sn = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(sn)) {
                deviceId.append("sn");
                deviceId.append(sn);
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
            String uuid = getUUID();
            if (!TextUtils.isEmpty(uuid)) {
                deviceId.append("id");
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId.append("id").append(getUUID());
        }
        return deviceId.toString();
    }

    /**
     * 优先读取本地uuid
     *
     * @return
     */
    private String getUUID() {
        SharedPreferences mShare = getSharedPreferences("uuid", MODE_PRIVATE);
        String uuid = "";
        if (mShare != null) {
            uuid = mShare.getString("uuid", "");
        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid", uuid).commit();
        }
        return uuid;
    }

    public String getSysInfo() {
        // 1. 手机品牌 2. 分辨率 3. 手机型号 4. SDK
        String brand = Build.BRAND;
        String pixel = this.getDeviceWidth(this) + "*" + this.getDeviceHeight(this);
        String model = Build.MODEL;
        int sdk = Build.VERSION.SDK_INT;
        String android_version = Build.VERSION.RELEASE;
        String language = Locale.getDefault().getLanguage();

        return brand + "|" + pixel + "|" + model + "|" + sdk + "|" + android_version + "|" + language;
    }

    /**
     * 获取设备宽度（px）
     */
    private int getDeviceWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取设备高度（px）
     */
    private int getDeviceHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
