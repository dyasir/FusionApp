package com.fusion.switchlib.http;

import android.text.TextUtils;

import androidx.lifecycle.LifecycleOwner;

import com.fusion.switchlib.http.custom.AseUtils;
import com.fusion.switchlib.model.ChangeBean;
import com.fusion.switchlib.model.ConfigBean;
import com.fusion.switchlib.model.DataMgr;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

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
    public static void getConfigs(LifecycleOwner activity, String url, String verName, String apiVersion, String packageName, String utm_source, String utm_medium, String install_time, String version,
                                  final HttpCallBack<ConfigBean> callBack) {
        newApi.getConfigs(url, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                verName, apiVersion, "1", DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                packageName, utm_source, utm_medium, install_time, version)
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
     * 获取融合配置
     *
     * @param activity
     * @param url
     * @param verName
     * @param apiVersion
     * @param packageName
     * @param callBack
     */
    public static void getChangeConfigs(LifecycleOwner activity, String url, String verName, String apiVersion, String packageName,
                                        final HttpCallBack<ChangeBean> callBack) {
        newApi.getChangeConfigs(url, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                verName, apiVersion, "1", DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "", packageName)
                .compose(RxSchedulers.io_main())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(new ApiObserver<String>() {

                    @Override
                    public void onSuccess(String demo, String msg) {
                        if (!TextUtils.isEmpty(demo)) {
                            ChangeBean changeBean = new Gson().fromJson(AseUtils.AseDecrypt(demo), ChangeBean.class);
                            Logger.json(new Gson().toJson(changeBean));
                            callBack.onSuccess(changeBean, msg);
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
    public static void stateChange(LifecycleOwner activity, String url, String verName, String apiVersion, String packageName, int event, final HttpCallBack<List<String>> callBack) {
        newApi.stateChange(url, DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getUdid() : "",
                verName, apiVersion, "1", DataMgr.getInstance().getUser() != null ? DataMgr.getInstance().getUser().getSysInfo() : "",
                packageName, DataMgr.getInstance().getUser().getToken(), event)
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
}
