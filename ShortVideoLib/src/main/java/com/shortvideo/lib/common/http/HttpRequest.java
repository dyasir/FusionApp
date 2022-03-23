package com.shortvideo.lib.common.http;

import android.text.TextUtils;

import androidx.lifecycle.LifecycleOwner;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.model.AdBean;
import com.shortvideo.lib.model.ConfigBean;
import com.shortvideo.lib.model.DataMgr;
import com.shortvideo.lib.model.FusionBean;
import com.shortvideo.lib.model.HomeBean;
import com.shortvideo.lib.model.VideoDetailBean;
import com.shortvideo.lib.model.VideoPathBean;
import com.shortvideo.lib.model.WallpaperBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class HttpRequest {

    static ApiRequest newApi;
    static ApiRequest wallpaperApi;
    static ApiRequest ipApi;

    static {
        newApi = RetrofitFactory.getInstance().initNewRetrofit().create(ApiRequest.class);
        wallpaperApi = RetrofitFactory.getInstance().initWallPaperRetrofit().create(ApiRequest.class);
        ipApi = RetrofitFactory.getInstance().initIpRetrofit().create(ApiRequest.class);
    }

    /**
     * 获取火热壁纸
     *
     * @param activity
     * @param limit
     * @param callBack
     */
    public static void getHotWallpaper(LifecycleOwner activity, int limit, final HttpCallBack<WallpaperBean> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("limit", limit);
        inputParamLog("获取火热壁纸", RetrofitFactory.WALLPAPER_URL, ApiRequest.HOT_WALLPAPER_URL, map);
        wallpaperApi.getHotWallpaper(limit)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new WallpaperApiObserver<WallpaperBean>() {

                    @Override
                    public void onSuccess(WallpaperBean demo, String msg) {
                        Logger.json(new Gson().toJson(demo));
                        callBack.onSuccess(demo, msg);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Logger.e(errorMsg);
                        callBack.onFail(errorCode, errorMsg);
                    }
                });
    }

    /**
     * 获取配置
     *
     * @param activity
     * @param callBack
     */
    public static void getConfigs(LifecycleOwner activity, String utm_source, String utm_medium, String install_time, String version,
                                  final HttpCallBack<ConfigBean> callBack) {
        inputParamLog("获取配置", RetrofitFactory.NEW_URL, VideoApplication.CONFIG_URL, new HashMap<>());
        newApi.getConfigs(VideoApplication.CONFIG_URL, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), VideoApplication.API_VERSION, "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                VideoApplication.getInstance().getPackageName(), utm_source, utm_medium, install_time, version)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<String>() {

                    @Override
                    public void onSuccess(String demo, String msg) {
                        if (!TextUtils.isEmpty(demo)) {
                            ConfigBean configBean = new Gson().fromJson(AseUtils.AseDecrypt(demo), ConfigBean.class);
                            Logger.json(new Gson().toJson(configBean));
                            callBack.onSuccess(configBean, msg);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Logger.e(errorMsg);
                        callBack.onFail(errorCode, errorMsg);
                    }
                });
    }

    /**
     * 首页视频
     *
     * @param activity
     * @param page
     * @param callBack
     */
    public static void getHomeVideo(LifecycleOwner activity, int page, final HttpCallBack<HomeBean> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        inputParamLog("首页视频", RetrofitFactory.NEW_URL, VideoApplication.HOME_VIDEO_URL, map);
        newApi.getHomeVideo(VideoApplication.HOME_VIDEO_URL, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), VideoApplication.API_VERSION, "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getToken() : "",
                VideoApplication.getInstance().getPackageName(), page)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<String>() {

                    @Override
                    public void onSuccess(String demo, String msg) {
                        if (!TextUtils.isEmpty(demo)) {
                            HomeBean homeBean = new Gson().fromJson(AseUtils.AseDecrypt(demo), HomeBean.class);
                            Logger.json(new Gson().toJson(homeBean));
                            callBack.onSuccess(homeBean, msg);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Logger.e(errorMsg);
                        callBack.onFail(errorCode, errorMsg);
                    }
                });
    }

    /**
     * 广告展示
     *
     * @param activity
     * @param id
     * @param type
     * @param callBack
     */
    public static void adShow(LifecycleOwner activity, int id, int type, final HttpCallBack<AdBean> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("type", type);
        inputParamLog("广告展示", RetrofitFactory.NEW_URL, VideoApplication.AD_SHOW_URL, map);
        newApi.adShow(VideoApplication.AD_SHOW_URL, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), VideoApplication.API_VERSION, "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getToken() : "",
                VideoApplication.getInstance().getPackageName(), id, type)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<String>() {

                    @Override
                    public void onSuccess(String demo, String msg) {
                        if (!TextUtils.isEmpty(demo)) {
                            AdBean adBean = new Gson().fromJson(AseUtils.AseDecrypt(demo), AdBean.class);
                            Logger.json(new Gson().toJson(adBean));
                            callBack.onSuccess(adBean, msg);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Logger.e(errorMsg);
                        callBack.onFail(errorCode, errorMsg);
                    }
                });
    }

    /**
     * 广告点击
     *
     * @param activity
     * @param id
     * @param callBack
     */
    public static void adClick(LifecycleOwner activity, int id, final HttpCallBack<AdBean> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        inputParamLog("广告点击", RetrofitFactory.NEW_URL, VideoApplication.AD_CLICK_URL, map);
        newApi.adClick(VideoApplication.AD_CLICK_URL, id)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<String>() {

                    @Override
                    public void onSuccess(String demo, String msg) {
                        if (!TextUtils.isEmpty(demo)) {
                            AdBean adBean = new Gson().fromJson(AseUtils.AseDecrypt(demo), AdBean.class);
                            Logger.json(new Gson().toJson(adBean));
                            callBack.onSuccess(adBean, msg);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Logger.e(errorMsg);
                        callBack.onFail(errorCode, errorMsg);
                    }
                });
    }

    /**
     * 点赞
     *
     * @param activity
     * @param vid
     * @param callBack
     */
    public static void goLike(LifecycleOwner activity, int vid, final HttpCallBack<List<String>> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("vid", vid);
        inputParamLog("点赞", RetrofitFactory.NEW_URL, VideoApplication.GO_LIKE_URL, map);
        newApi.goLike(VideoApplication.GO_LIKE_URL, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), VideoApplication.API_VERSION, "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getToken() : "",
                VideoApplication.getInstance().getPackageName(), vid)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<String>() {

                    @Override
                    public void onSuccess(String demo, String msg) {
                        Logger.d(demo);
                        List<String> list = new ArrayList<>();
                        callBack.onSuccess(list, msg);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Logger.e(errorMsg);
                        if (errorCode == 621) {
                            callBack.onFail(errorCode, VideoApplication.getInstance().getString(R.string.tk_user_liked));
                        } else {
                            callBack.onFail(errorCode, errorMsg);
                        }
                    }
                });
    }

    /**
     * 取消点赞
     *
     * @param activity
     * @param vid
     * @param callBack
     */
    public static void cancelLike(LifecycleOwner activity, int vid, final HttpCallBack<List<String>> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("vid", vid);
        inputParamLog("取消点赞", RetrofitFactory.NEW_URL, VideoApplication.CANCEL_LIKE_URL, map);
        newApi.cancelLike(VideoApplication.CANCEL_LIKE_URL, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), VideoApplication.API_VERSION, "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getToken() : "",
                VideoApplication.getInstance().getPackageName(), vid)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<String>() {

                    @Override
                    public void onSuccess(String demo, String msg) {
                        Logger.d(demo);
                        List<String> list = new ArrayList<>();
                        callBack.onSuccess(list, msg);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Logger.e(errorMsg);
                        callBack.onFail(errorCode, errorMsg);
                    }
                });
    }

    /**
     * 获取下载地址
     *
     * @param activity
     * @param vid
     * @param callBack
     */
    public static void getDownLoadPath(LifecycleOwner activity, int vid, final HttpCallBack<VideoPathBean> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("vid", vid);
        inputParamLog("获取下载地址", RetrofitFactory.NEW_URL, VideoApplication.DOWNLOAD_PATH_URL, map);
        newApi.getDownLoadPath(VideoApplication.DOWNLOAD_PATH_URL, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), VideoApplication.API_VERSION, "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                VideoApplication.getInstance().getPackageName(), vid)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<String>() {

                    @Override
                    public void onSuccess(String demo, String msg) {
                        if (!TextUtils.isEmpty(demo)) {
                            VideoPathBean videoPathBean = new Gson().fromJson(AseUtils.AseDecrypt(demo), VideoPathBean.class);
                            Logger.json(new Gson().toJson(videoPathBean));
                            callBack.onSuccess(videoPathBean, msg);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Logger.e(errorMsg);
                        callBack.onFail(errorCode, errorMsg);
                    }
                });
    }

    /**
     * 举报
     *
     * @param activity
     * @param content
     * @param callBack
     */
    public static void report(LifecycleOwner activity, String content, final HttpCallBack<List<String>> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("content", content);
        inputParamLog("举报", RetrofitFactory.NEW_URL, VideoApplication.REPORT_URL, map);
        newApi.report(VideoApplication.REPORT_URL, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), VideoApplication.API_VERSION, "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                VideoApplication.getInstance().getPackageName(), content)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<String>() {

                    @Override
                    public void onSuccess(String demo, String msg) {
                        Logger.d(demo);
                        List<String> list = new ArrayList<>();
                        callBack.onSuccess(list, msg);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Logger.e(errorMsg);
                        callBack.onFail(errorCode, errorMsg);
                    }
                });
    }

    /**
     * 视频详情
     *
     * @param activity
     * @param id
     * @param f
     * @param callBack
     */
    public static void getVideoDetail(LifecycleOwner activity, int id, String f, final HttpCallBack<VideoDetailBean> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("f", f);
        inputParamLog("视频详情", RetrofitFactory.NEW_URL, VideoApplication.VIDEO_DETAIL_URL, map);
        newApi.getVideoDetail(VideoApplication.VIDEO_DETAIL_URL, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), VideoApplication.API_VERSION, "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getToken() : "",
                VideoApplication.getInstance().getPackageName(), id, f)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<String>() {

                    @Override
                    public void onSuccess(String demo, String msg) {
                        if (!TextUtils.isEmpty(demo)) {
                            VideoDetailBean videoDetailBean = new Gson().fromJson(AseUtils.AseDecrypt(demo), VideoDetailBean.class);
                            Logger.json(new Gson().toJson(videoDetailBean));
                            callBack.onSuccess(videoDetailBean, msg);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Logger.e(errorMsg);
                        callBack.onFail(errorCode, errorMsg);
                    }
                });
    }

    /**
     * 获取融合APP配置
     *
     * @param activity
     * @param callBack
     */
    public static void getFusion(LifecycleOwner activity, final HttpCallBack<FusionBean> callBack) {
        inputParamLog("获取融合APP配置", RetrofitFactory.NEW_URL, VideoApplication.FUSION_URL, null);
        newApi.getFusion(VideoApplication.FUSION_URL, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), VideoApplication.API_VERSION, "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                VideoApplication.getInstance().getPackageName())
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<String>() {

                    @Override
                    public void onSuccess(String demo, String msg) {
                        if (!TextUtils.isEmpty(demo)) {
                            FusionBean fusionBean = new Gson().fromJson(AseUtils.AseDecrypt(demo), FusionBean.class);
                            Logger.json(new Gson().toJson(fusionBean));
                            callBack.onSuccess(fusionBean, msg);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Logger.e(errorMsg);
                        callBack.onFail(errorCode, errorMsg);
                    }
                });
    }

    /**
     * 记录切换APP
     *
     * @param activity
     * @param event
     * @param callBack
     */
    public static void stateChange(LifecycleOwner activity, int event, final HttpCallBack<List<String>> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("event", event);
        inputParamLog("记录切换APP", RetrofitFactory.NEW_URL, VideoApplication.STATE_CHANGE_URL, map);
        newApi.stateChange(VideoApplication.STATE_CHANGE_URL, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), VideoApplication.API_VERSION, "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                VideoApplication.getInstance().getPackageName(), DataMgr.getInstance().getUser().getToken(), event)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<String>() {

                    @Override
                    public void onSuccess(String demo, String msg) {
                        Logger.d(demo);
                        List<String> list = new ArrayList<>();
                        callBack.onSuccess(list, msg);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Logger.e(errorMsg);
                        callBack.onFail(errorCode, errorMsg);
                    }
                });
    }

    /**
     * 根据本地ip获取地域
     *
     * @param activity
     * @param callBack
     */
    public static void getIpCountry(LifecycleOwner activity, final HttpCallBack<String> callBack) {
        ipApi.getIpCountry()
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new IPApiObserver<String>() {

                    @Override
                    public void onSuccess(String country, String countryCode) {
                        Logger.d("country: " + country + "\ncountryCode: " + countryCode);
                        callBack.onSuccess(country, countryCode);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Logger.e(errorMsg);
                        callBack.onFail(errorCode, errorMsg);
                    }
                });
    }

    /**
     * 格式化入参(Debug模式下打印)
     *
     * @param apiName 接口名称
     * @param baseUrl 域名
     * @param apiUrl  api地址
     * @param map     参数
     */
    private static void inputParamLog(String apiName, String baseUrl, String apiUrl, Map<String, Object> map) {
        if (VideoApplication.getInstance().isDebugMode())
            Logger.i("--> " + apiName + ":入参\n--> url:" + baseUrl + apiUrl
                    + "\n--> parms:" + (map == null ? "" : map) + (DataMgr.getInstance().getUser() != null ?
                    "\n--> headers:{\n             \"token\":\"" + DataMgr.getInstance().getUser().getToken() + "\"" +
                            "\n             \"udid\":\"" + DataMgr.getInstance().getUser().getUdid() + "\"" +
                            "\n             \"sys-info\":\"" + DataMgr.getInstance().getUser().getSysInfo() + "\"" +
                            "\n             \"package-id\":\"" + VideoApplication.getInstance().getPackageName() + "\"" +
                            "\n            }" : ""));
    }
}
