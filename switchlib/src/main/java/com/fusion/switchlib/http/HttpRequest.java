package com.fusion.switchlib.http;

import androidx.lifecycle.LifecycleOwner;

import com.fusion.switchlib.SwitchApplication;
import com.fusion.switchlib.model.ConfigBean;
import com.fusion.switchlib.model.DataMgr;
import com.fusion.switchlib.model.FusionBean;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

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
                SwitchApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                SwitchApplication.getInstance().getPackageName(), utm_source, utm_medium, install_time, version)
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
     * 获取融合APP配置
     *
     * @param activity
     * @param callBack
     */
    public static void getFusion(LifecycleOwner activity, final HttpCallBack<FusionBean> callBack) {
        inputParamLog("获取融合APP配置", RetrofitFactory.NEW_URL, ApiRequest.FUSION_URL, null);
        newApi.getFusion(DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                SwitchApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                SwitchApplication.getInstance().getPackageName())
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
                SwitchApplication.getInstance().getVerName(), "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                SwitchApplication.getInstance().getPackageName(), DataMgr.getInstance().getUser().getToken(), event)
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
        if (SwitchApplication.getInstance().isDebugMode())
            Logger.i("--> " + apiName + ":入参\n--> url:" + baseUrl + apiUrl
                    + "\n--> parms:" + (map == null ? "" : map) + (DataMgr.getInstance().getUser() != null ?
                    "\n--> headers:{\n             \"token\":\"" + DataMgr.getInstance().getUser().getToken() + "\"" +
                            "\n             \"udid\":\"" + DataMgr.getInstance().getUser().getUdid() + "\"" +
                            "\n             \"sys-info\":\"" + DataMgr.getInstance().getUser().getSysInfo() + "\"" +
                            "\n             \"package-id\":\"" + SwitchApplication.getInstance().getPackageName() + "\"" +
                            "\n            }" : ""));
    }
}
