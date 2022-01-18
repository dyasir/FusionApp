package com.shortvideo.lib.common.http;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class HttpRequest {

    static ApiRequest newApi;

    static {
        newApi = RetrofitFactory.getInstance().initNewRetrofit().create(ApiRequest.class);
    }

    /**
     * 获取配置
     *
     * @param activity
     * @param callBack
     */
    public static void getConfigs(LifecycleOwner activity, String utm_source, String utm_medium, String install_time, String version,
                                  final HttpCallBack<ConfigBean> callBack) {
        inputParamLog("获取配置", RetrofitFactory.NEW_URL, ApiRequest.CONFIG_URL, new HashMap<>());
        newApi.getConfigs(DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                VideoApplication.getInstance().getPackageName(), utm_source, utm_medium, install_time, version)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<ConfigBean>() {

                    @Override
                    public void onSuccess(ConfigBean demo, String msg) {
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
     * 首页视频
     *
     * @param activity
     * @param page
     * @param callBack
     */
    public static void getHomeVideo(LifecycleOwner activity, int page, final HttpCallBack<HomeBean> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        inputParamLog("首页视频", RetrofitFactory.NEW_URL, ApiRequest.HOME_VIDEO_URL, map);
        newApi.getHomeVideo(DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getToken() : "",
                VideoApplication.getInstance().getPackageName(), page)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<HomeBean>() {

                    @Override
                    public void onSuccess(HomeBean demo, String msg) {
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
        inputParamLog("广告展示", RetrofitFactory.NEW_URL, ApiRequest.AD_SHOW_URL, map);
        newApi.adShow(DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getToken() : "",
                VideoApplication.getInstance().getPackageName(), id, type)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<AdBean>() {

                    @Override
                    public void onSuccess(AdBean demo, String msg) {
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
     * 广告点击
     *
     * @param activity
     * @param id
     * @param callBack
     */
    public static void adClick(LifecycleOwner activity, int id, final HttpCallBack<AdBean> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        inputParamLog("广告点击", RetrofitFactory.NEW_URL, ApiRequest.AD_CLICK_URL, map);
        newApi.adClick(id)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<AdBean>() {

                    @Override
                    public void onSuccess(AdBean demo, String msg) {
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
     * 点赞
     *
     * @param activity
     * @param vid
     * @param callBack
     */
    public static void goLike(LifecycleOwner activity, int vid, final HttpCallBack<List<String>> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("vid", vid);
        inputParamLog("点赞", RetrofitFactory.NEW_URL, ApiRequest.GO_LIKE_URL, map);
        newApi.goLike(DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getToken() : "",
                VideoApplication.getInstance().getPackageName(), vid)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<List<String>>() {

                    @Override
                    public void onSuccess(List<String> demo, String msg) {
                        Logger.d(demo);
                        callBack.onSuccess(demo, msg);
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
        inputParamLog("取消点赞", RetrofitFactory.NEW_URL, ApiRequest.CANCEL_LIKE_URL, map);
        newApi.cancelLike(DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getToken() : "",
                VideoApplication.getInstance().getPackageName(), vid)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<List<String>>() {

                    @Override
                    public void onSuccess(List<String> demo, String msg) {
                        Logger.d(demo);
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
     * 获取下载地址
     *
     * @param activity
     * @param vid
     * @param callBack
     */
    public static void getDownLoadPath(LifecycleOwner activity, int vid, final HttpCallBack<VideoPathBean> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("vid", vid);
        inputParamLog("获取下载地址", RetrofitFactory.NEW_URL, ApiRequest.DOWNLOAD_PATH_URL, map);
        newApi.getDownLoadPath(DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                VideoApplication.getInstance().getPackageName(), vid)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<VideoPathBean>() {

                    @Override
                    public void onSuccess(VideoPathBean demo, String msg) {
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
     * 举报
     *
     * @param activity
     * @param content
     * @param callBack
     */
    public static void report(LifecycleOwner activity, String content, final HttpCallBack<List<String>> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("content", content);
        inputParamLog("举报", RetrofitFactory.NEW_URL, ApiRequest.REPORT_URL, map);
        newApi.report(DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                VideoApplication.getInstance().getPackageName(), content)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<List<String>>() {

                    @Override
                    public void onSuccess(List<String> demo, String msg) {
                        Logger.d(demo);
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
        inputParamLog("视频详情", RetrofitFactory.NEW_URL, ApiRequest.VIDEO_DETAIL_URL, map);
        newApi.getVideoDetail(DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getToken() : "",
                VideoApplication.getInstance().getPackageName(), id, f)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<VideoDetailBean>() {

                    @Override
                    public void onSuccess(VideoDetailBean demo, String msg) {
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
     * 获取融合APP配置
     *
     * @param activity
     * @param callBack
     */
    public static void getFusion(LifecycleOwner activity, final HttpCallBack<FusionBean> callBack) {
        inputParamLog("获取融合APP配置", RetrofitFactory.NEW_URL, ApiRequest.FUSION_URL, null);
        newApi.getFusion(DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                VideoApplication.getInstance().getPackageName())
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<FusionBean>() {

                    @Override
                    public void onSuccess(FusionBean demo, String msg) {
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
     * 记录切换APP
     *
     * @param activity
     * @param event
     * @param callBack
     */
    public static void stateChange(LifecycleOwner activity, int event, final HttpCallBack<List<String>> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("event", event);
        inputParamLog("记录切换APP", RetrofitFactory.NEW_URL, ApiRequest.STATE_CHANGE_URL, map);
        newApi.stateChange(DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                VideoApplication.getInstance().getPackageName(), DataMgr.getInstance().getUser().getToken(), event)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<List<String>>() {

                    @Override
                    public void onSuccess(List<String> demo, String msg) {
                        Logger.d(demo);
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
     * 上传本机号码
     *
     * @param activity
     * @param local_mobile
     * @param callBack
     */
    public static void uploadContacts(LifecycleOwner activity, String local_mobile, final HttpCallBack<List<String>> callBack) {
        Map<String, Object> map = new HashMap<>();
        map.put("local_mobile", local_mobile);
        inputParamLog("上传本机号码", RetrofitFactory.NEW_URL, ApiRequest.CONTACTS_URL, map);
        newApi.uploadContacts(DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                VideoApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                VideoApplication.getInstance().getPackageName(), DataMgr.getInstance().getUser().getToken(), local_mobile)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<List<String>>() {

                    @Override
                    public void onSuccess(List<String> demo, String msg) {
                        Logger.d(demo);
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
