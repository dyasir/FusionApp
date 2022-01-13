package com.shortvideo.lib;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.shortvideo.lib.common.TkAppConfig;
import com.shortvideo.lib.common.http.RetrofitFactory;
import com.shortvideo.lib.utils.LogCatStrategy;
import com.shortvideo.lib.utils.SPUtils;
import com.shortvideo.lib.utils.ToastyUtils;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.tencent.bugly.crashreport.CrashReport;

import net.mikaelzero.mojito.Mojito;
import net.mikaelzero.mojito.loader.glide.GlideImageLoader;
import net.mikaelzero.mojito.view.sketch.SketchImageLoadFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

public class VideoApplication extends Application {

    private static VideoApplication application;

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

    //是否是生产环境
    private boolean isProduct = false;
    //选择加载视频布局1，或布局2
    private int videoLayoutType = 1;
    //是否添加纯享功能
    private boolean isPureEnjoyment = false;

    //跳转视频APP的路由path
    public static final String SHORT_VIDEO_PATH = "/videolib/videosplash";
    //跳转第三方APP的路由path
    public static final String THIRD_ROUTE_PATH = "/third/mainactivity";

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

    public boolean isProduct() {
        return isProduct;
    }

    public void setProduct(boolean product) {
        isProduct = product;
    }

    public int getVideoLayoutType() {
        return videoLayoutType;
    }

    public void setVideoLayoutType(int videoLayoutType) {
        this.videoLayoutType = videoLayoutType > 2 ? 2 : Math.max(videoLayoutType, 1);
    }

    public boolean isPureEnjoyment() {
        return isPureEnjoyment;
    }

    public void setPureEnjoyment(boolean pureEnjoyment) {
        isPureEnjoyment = pureEnjoyment;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        //初始化路由
        ARouter.init(this);

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

        //初始化Bugly
        initBugly();

        //初始化图片查看器
        initMojito();

        //初始化Api域名
        initVideoApiUrl();
    }

    /**
     * 初始化Api域名
     * 以及一些功能页面配置
     */
    protected void initVideoApiUrl() {
        //是否为生产环境
        setProduct(false);
        //默认域名
        RetrofitFactory.NEW_URL = "http://172.247.143.109:85/";
        //视频使用布局1
        setVideoLayoutType(1);
        //关闭纯享功能
        setPureEnjoyment(false);
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
     * 初始化bugly
     */
    private void initBugly() {
        //获取当前包名
        String packageName = getPackageName();
        //获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        //设置是否为上报进程
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        //设置设备唯一码
        strategy.setDeviceID(getUDID());
        //设置设备型号
        strategy.setDeviceModel(Build.BRAND + " " + Build.MODEL);
        CrashReport.initCrashReport(this, TkAppConfig.BUGLY_ID, true, strategy);
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
    private String getUDID() {
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
}
