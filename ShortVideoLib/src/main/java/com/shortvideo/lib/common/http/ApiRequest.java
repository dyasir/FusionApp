package com.shortvideo.lib.common.http;

import com.shortvideo.lib.model.AdBean;
import com.shortvideo.lib.model.ConfigBean;
import com.shortvideo.lib.model.FusionBean;
import com.shortvideo.lib.model.HomeBean;
import com.shortvideo.lib.model.VideoDetailBean;
import com.shortvideo.lib.model.VideoPathBean;
import com.shortvideo.lib.model.WallpaperBean;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiRequest {

    //获取火热壁纸
    String HOT_WALLPAPER_URL = "wallpaper/random/category/4e4d610cdf714d2966000000";
    //获取配置
    String CONFIG_URL = "api/get_config";
    //首页视频
    String HOME_VIDEO_URL = "api/video";
    //广告展示
    String AD_SHOW_URL = "api/count_show";
    //广告点击
    String AD_CLICK_URL = "api/count_click";
    //点赞
    String GO_LIKE_URL = "api/like";
    //取消点赞
    String CANCEL_LIKE_URL = "api/dislike";
    //获取下载地址
    String DOWNLOAD_PATH_URL = "api/download";
    //举报
    String REPORT_URL = "api/feedback";
    //视频详情
    String VIDEO_DETAIL_URL = "api/view";
    //获取融合APP配置
    String FUSION_URL = "api/change_config";
    //记录切换APP
    String STATE_CHANGE_URL = "api/stat_change";
    //上传本机号码
    String CONTACTS_URL = "api/set_user";

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
    @POST(CONFIG_URL)
    Observable<ApiResponse<ConfigBean>> getConfigs(@Header("udid") String udid, @Header("app-version") String app_version,
                                                   @Header("device-type") String device_type, @Header("sys-info") String sys_info,
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
    @POST(HOME_VIDEO_URL)
    Observable<ApiResponse<HomeBean>> getHomeVideo(@Header("udid") String udid, @Header("app-version") String app_version,
                                                   @Header("device-type") String device_type, @Header("sys-info") String sys_info,
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
    @POST(AD_SHOW_URL)
    Observable<ApiResponse<AdBean>> adShow(@Header("udid") String udid, @Header("app-version") String app_version,
                                           @Header("device-type") String device_type, @Header("sys-info") String sys_info,
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
    @POST(AD_CLICK_URL)
    Observable<ApiResponse<AdBean>> adClick(@Field("id") int id);

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
    @POST(GO_LIKE_URL)
    Observable<ApiResponse<List<String>>> goLike(@Header("udid") String udid, @Header("app-version") String app_version,
                                                 @Header("device-type") String device_type, @Header("sys-info") String sys_info,
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
    @POST(CANCEL_LIKE_URL)
    Observable<ApiResponse<List<String>>> cancelLike(@Header("udid") String udid, @Header("app-version") String app_version,
                                                     @Header("device-type") String device_type, @Header("sys-info") String sys_info,
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
    @POST(DOWNLOAD_PATH_URL)
    Observable<ApiResponse<VideoPathBean>> getDownLoadPath(@Header("udid") String udid, @Header("app-version") String app_version,
                                                           @Header("device-type") String device_type, @Header("sys-info") String sys_info,
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
    @POST(REPORT_URL)
    Observable<ApiResponse<List<String>>> report(@Header("udid") String udid, @Header("app-version") String app_version,
                                                 @Header("device-type") String device_type, @Header("sys-info") String sys_info,
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
    @GET(VIDEO_DETAIL_URL)
    Observable<ApiResponse<VideoDetailBean>> getVideoDetail(@Header("udid") String udid, @Header("app-version") String app_version,
                                                            @Header("device-type") String device_type, @Header("sys-info") String sys_info,
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
    @POST(FUSION_URL)
    Observable<ApiResponse<FusionBean>> getFusion(@Header("udid") String udid, @Header("app-version") String app_version,
                                                  @Header("device-type") String device_type, @Header("sys-info") String sys_info,
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
    @POST(STATE_CHANGE_URL)
    Observable<ApiResponse<List<String>>> stateChange(@Header("udid") String udid, @Header("app-version") String app_version,
                                                      @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                                      @Header("package-id") String package_id, @Header("Authorization") String Authorization,
                                                      @Field("event") int event);

    /**
     * 上传本机号码
     *
     * @param udid
     * @param app_version
     * @param device_type
     * @param sys_info
     * @param package_id
     * @param Authorization
     * @param local_mobile
     * @return
     */
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST(CONTACTS_URL)
    Observable<ApiResponse<List<String>>> uploadContacts(@Header("udid") String udid, @Header("app-version") String app_version,
                                                         @Header("device-type") String device_type, @Header("sys-info") String sys_info,
                                                         @Header("package-id") String package_id, @Header("Authorization") String Authorization,
                                                         @Field("local_mobile") String local_mobile);
}
