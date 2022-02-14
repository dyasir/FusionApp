package com.fusion.switchlib.http;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiRequest {

    /**
     * 获取配置
     *
     * @param udid
     * @param app_version
     * @param device_type
     * @param sys_info
     * @param package_id
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("api/{CONFIG_URL}")
    Observable<ApiResponse<String>> getConfigs(@Path("CONFIG_URL") String CONFIG_URL, @Header("udid") String udid,
                                               @Header("app-version") String app_version, @Header("api-version") String api_version,
                                               @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                               @Header("package-id") String package_id, @Field("utm_source") String utm_source,
                                               @Field("utm_medium") String utm_medium, @Field("install_time") String install_time,
                                               @Field("version") String version);

    /**
     * 记录切换APP
     *
     * @param udid
     * @param app_version
     * @param device_type
     * @param sys_info
     * @param package_id
     * @param event       1：视频   2：广告   3: 时长   4：启动   5：立即
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("api/{STATE_CHANGE_URL}")
    Observable<ApiResponse<String>> stateChange(@Path("STATE_CHANGE_URL") String STATE_CHANGE_URL, @Header("udid") String udid,
                                                @Header("app-version") String app_version, @Header("api-version") String api_version,
                                                @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                                @Header("package-id") String package_id, @Header("Authorization") String Authorization,
                                                @Field("event") int event);
}
