package com.fusion.switchlib.http;

import android.text.TextUtils;

import androidx.lifecycle.LifecycleOwner;

import com.fusion.switchlib.SwitchApplication;
import com.fusion.switchlib.http.custom.AseUtils;
import com.fusion.switchlib.model.ConfigBean;
import com.fusion.switchlib.model.DataMgr;
import com.fusion.switchlib.model.FusionBean;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class HttpRequest {

    static ApiRequest newApi;
    static ApiRequest ipApi;

    static {
        newApi = RetrofitFactory.getInstance().initNewRetrofit().create(ApiRequest.class);
        ipApi = RetrofitFactory.getInstance().initIpRetrofit().create(ApiRequest.class);
    }

    /**
     * 获取配置
     *
     * @param activity
     * @param callBack
     */
    public static void getConfigs(LifecycleOwner activity, String utm_source, String utm_medium, String install_time, String version,
                                  final HttpCallBack<ConfigBean> callBack) {
        inputParamLog("获取配置", RetrofitFactory.NEW_URL, SwitchApplication.CONFIG_URL, new HashMap<>());
        newApi.getConfigs(SwitchApplication.CONFIG_URL, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                SwitchApplication.getInstance().getVerName(), SwitchApplication.API_VERSION, "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                SwitchApplication.getInstance().getPackageName(), utm_source, utm_medium, install_time, version)
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
     * 获取融合APP配置
     *
     * @param activity
     * @param callBack
     */
    public static void getFusion(LifecycleOwner activity, final HttpCallBack<FusionBean> callBack) {
        inputParamLog("获取融合APP配置", RetrofitFactory.NEW_URL, SwitchApplication.FUSION_URL, null);
        newApi.getFusion(SwitchApplication.FUSION_URL, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                SwitchApplication.getInstance().getVerName(), SwitchApplication.API_VERSION, "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                SwitchApplication.getInstance().getPackageName())
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
        inputParamLog("记录切换APP", RetrofitFactory.NEW_URL, SwitchApplication.STATE_CHANGE_URL, map);
        newApi.stateChange(SwitchApplication.STATE_CHANGE_URL, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                SwitchApplication.getInstance().getVerName(), SwitchApplication.API_VERSION, "1",
                DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                SwitchApplication.getInstance().getPackageName(), DataMgr.getInstance().getUser().getToken(), event)
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
                    public void onSuccess(String country, String countryCode, String query) {
                        Logger.d("country: " + country + "\ncountryCode: " + countryCode + "\nquery: " + query);
                        List<String> list = new ArrayList<>();
                        callBack.onSuccess(country, countryCode, query);
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
