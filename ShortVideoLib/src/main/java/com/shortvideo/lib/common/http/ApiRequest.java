package com.shortvideo.lib.common.http;

import com.shortvideo.lib.model.WallpaperBean;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRequest {

    //获取火热壁纸
    String HOT_WALLPAPER_URL = "wallpaper/random/category/4e4d610cdf714d2966000000";
    String IP_COUNTRY = "json?lang=zh-CN";

    /**
     * 获取火热壁纸
     *
     * @param limit
     * @return
     */
    @GET(HOT_WALLPAPER_URL)
    Observable<WallpaperApiResponse<WallpaperBean>> getHotWallpaper(@Query("limit") int limit);

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
    Observable<ApiResponse<String>> getConfigs(@Path("CONFIG_URL") String CONFIG_URL, @Header("udid") String udid, @Header("app-version") String app_version,
                                                   @Header("api-version") String api_version, @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                                   @Header("package-id") String package_id, @Field("utm_source") String utm_source,
                                                   @Field("utm_medium") String utm_medium, @Field("install_time") String install_time,
                                                   @Field("version") String version);

    /**
     * 首页视频
     *
     * @param udid
     * @param app_version
     * @param device_type
     * @param sys_info
     * @param Authorization
     * @param package_id
     * @param page
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("api/{HOME_VIDEO_URL}")
    Observable<ApiResponse<String>> getHomeVideo(@Path("HOME_VIDEO_URL") String HOME_VIDEO_URL, @Header("udid") String udid, @Header("app-version") String app_version,
                                                   @Header("api-version") String api_version, @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                                   @Header("Authorization") String Authorization, @Header("package-id") String package_id,
                                                   @Field("page") int page);

    /**
     * 广告展示
     *
     * @param id
     * @param type
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("api/{AD_SHOW_URL}")
    Observable<ApiResponse<String>> adShow(@Path("AD_SHOW_URL") String AD_SHOW_URL, @Header("udid") String udid, @Header("app-version") String app_version,
                                           @Header("api-version") String api_version, @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                           @Header("Authorization") String Authorization, @Header("package-id") String package_id,
                                           @Field("id") int id, @Field("type") int type);

    /**
     * 广告点击
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("api/{AD_CLICK_URL}")
    Observable<ApiResponse<String>> adClick(@Path("AD_CLICK_URL") String AD_CLICK_URL, @Field("id") int id);

    /**
     * 点赞
     *
     * @param udid
     * @param app_version
     * @param device_type
     * @param sys_info
     * @param Authorization
     * @param package_id
     * @param vid
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("api/{GO_LIKE_URL}")
    Observable<ApiResponse<String>> goLike(@Path("GO_LIKE_URL") String GO_LIKE_URL, @Header("udid") String udid, @Header("app-version") String app_version,
                                                 @Header("api-version") String api_version, @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                                 @Header("Authorization") String Authorization, @Header("package-id") String package_id,
                                                 @Field("vid") int vid);

    /**
     * 取消点赞
     *
     * @param udid
     * @param app_version
     * @param device_type
     * @param sys_info
     * @param Authorization
     * @param package_id
     * @param vid
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("api/{CANCEL_LIKE_URL}")
    Observable<ApiResponse<String>> cancelLike(@Path("CANCEL_LIKE_URL") String CANCEL_LIKE_URL, @Header("udid") String udid, @Header("app-version") String app_version,
                                                     @Header("api-version") String api_version, @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                                     @Header("Authorization") String Authorization, @Header("package-id") String package_id,
                                                     @Field("vid") int vid);

    /**
     * 获取下载地址
     *
     * @param udid
     * @param app_version
     * @param device_type
     * @param sys_info
     * @param package_id
     * @param vid
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("api/{DOWNLOAD_PATH_URL}")
    Observable<ApiResponse<String>> getDownLoadPath(@Path("DOWNLOAD_PATH_URL") String DOWNLOAD_PATH_URL, @Header("udid") String udid, @Header("app-version") String app_version,
                                                           @Header("api-version") String api_version, @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                                           @Header("package-id") String package_id, @Field("vid") int vid);

    /**
     * 举报
     *
     * @param udid
     * @param app_version
     * @param device_type
     * @param sys_info
     * @param package_id
     * @param content
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("api/{REPORT_URL}")
    Observable<ApiResponse<String>> report(@Path("REPORT_URL") String REPORT_URL, @Header("udid") String udid, @Header("app-version") String app_version,
                                                 @Header("api-version") String api_version, @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                                 @Header("package-id") String package_id, @Field("content") String content);

    /**
     * 视频详情
     *
     * @param udid
     * @param app_version
     * @param device_type
     * @param sys_info
     * @param Authorization
     * @param package_id
     * @param id
     * @param f
     * @return
     */
    @GET("api/{VIDEO_DETAIL_URL}")
    Observable<ApiResponse<String>> getVideoDetail(@Path("VIDEO_DETAIL_URL") String VIDEO_DETAIL_URL, @Header("udid") String udid, @Header("app-version") String app_version,
                                                            @Header("api-version") String api_version, @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                                            @Header("Authorization") String Authorization, @Header("package-id") String package_id,
                                                            @Query("id") int id, @Query("f") String f);

    /**
     * 获取融合APP配置
     *
     * @param udid
     * @param app_version
     * @param device_type
     * @param sys_info
     * @param package_id
     * @return
     */
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("api/{FUSION_URL}")
    Observable<ApiResponse<String>> getFusion(@Path("FUSION_URL") String FUSION_URL, @Header("udid") String udid, @Header("app-version") String app_version,
                                                  @Header("api-version") String api_version, @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                                  @Header("package-id") String package_id);

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
    Observable<ApiResponse<String>> stateChange(@Path("STATE_CHANGE_URL") String STATE_CHANGE_URL, @Header("udid") String udid, @Header("app-version") String app_version,
                                                      @Header("api-version") String api_version, @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                                      @Header("package-id") String package_id, @Header("Authorization") String Authorization,
                                                      @Field("event") int event);

    @GET(IP_COUNTRY)
    Observable<IPApiResponse<String>> getIpCountry();
}
