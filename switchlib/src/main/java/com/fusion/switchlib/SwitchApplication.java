package com.fusion.switchlib;

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
import com.fusion.switchlib.http.RetrofitFactory;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

public class SwitchApplication extends Application {

    private static SwitchApplication application;

    //Firebase
    private FirebaseAnalytics mFirebaseAnalytics;
    //来源统计
    private String utm_source;
    private String utm_medium;
    private String utm_install_time;
    private String utm_version;
    //是否越南地区
    private String notvi_update_enable;
    private String is_vi;
    //是否是生产环境
    private boolean isProduct;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        //初始化sp
        SPUtils.init(this);

        //log初始化
        initLog();

        //连接Google pay分析来源
        connectGooglePlay();

        //初始化Google统计
        initFirebase();

        //配置环境以及备用域名
        initSwitchConfig();
    }

    /**
     * 配置环境以及备用域名
     */
    protected void initSwitchConfig() {
        setDefaultUrl("http://172.247.143.109:85/")
                .setProduct(false);
    }

    /**
     * 配置环境
     *
     * @param product
     */
    public void setProduct(boolean product) {
        isProduct = product;
    }

    /**
     * 备用域名
     *
     * @param defaultUrl
     */
    public SwitchApplication setDefaultUrl(String defaultUrl) {
        RetrofitFactory.NEW_URL = defaultUrl;
        return application;
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

    public static SwitchApplication getInstance() {
        return application;
    }

    public FirebaseAnalytics getmFirebaseAnalytics() {
        return mFirebaseAnalytics;
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

    public String getNotvi_update_enable() {
        return notvi_update_enable;
    }

    public void setNotvi_update_enable(String notvi_update_enable) {
        this.notvi_update_enable = notvi_update_enable;
    }

    public String getIs_vi() {
        return is_vi;
    }

    public void setIs_vi(String is_vi) {
        this.is_vi = is_vi;
    }

    public boolean isProduct() {
        return isProduct;
    }
}
