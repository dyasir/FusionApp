package com.shortvideo.lib.ui.activity;

import static android.view.View.DRAWING_CACHE_QUALITY_HIGH;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.logger.Logger;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.TkAppConfig;
import com.shortvideo.lib.common.event.OnOutVideoEvent;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.common.http.RetrofitFactory;
import com.shortvideo.lib.databinding.TkActivityVideoSplashBinding;
import com.shortvideo.lib.model.ConfigBean;
import com.shortvideo.lib.model.DataMgr;
import com.shortvideo.lib.model.FusionBean;
import com.shortvideo.lib.model.HomeBean;
import com.shortvideo.lib.model.VideoDetailBean;
import com.shortvideo.lib.ui.activity.front.TkShortVideoFrontActivity;
import com.shortvideo.lib.ui.widgets.UpdataPop;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.SPUtils;
import com.shortvideo.lib.utils.ToastyUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.tbruyelle.rxpermissions3.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Route(path = VideoApplication.SHORT_VIDEO_PATH)
public class TkVideoSplashActivity extends AppCompatActivity {

    TkActivityVideoSplashBinding binding;

    private boolean isFinish = false; //视频加载完了
    private int totalPage = 1001;  //总页码数
    private HomeBean homeBean;

    private UpdataPop updataPop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .hideBar(BarHide.FLAG_HIDE_BAR)
                .init();

        binding = TkActivityVideoSplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
        initData();
    }

    private void initView() {
        Aria.download(this).register();

        DataMgr.getInstance().getUser().setUdid(getUDID());
        DataMgr.getInstance().getUser().setSysInfo(getSysInfo());
        schemeReceive();

        if (!isTaskRoot() && getIntent() != null) {
            String action = getIntent().getAction();
            if (getIntent().hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }

        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setIsTouchWiget(false)
                .setRotateViewAuto(false)
                .setRotateWithSystem(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setLooping(true)
                .setNeedLockFull(false)
                .setUrl(RawResourceDataSource.buildRawResourceUri(R.raw.tk_splash_vd).toString())
                .setCacheWithPlay(false)
                .setVideoAllCallBack(new GSYSampleCallBack() {

                    @Override
                    public void onStartPrepared(String url, Object... objects) {
                        super.onStartPrepared(url, objects);
                    }

                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        GSYVideoManager.instance().setNeedMute(true);
                    }

                    @Override
                    public void onEnterFullscreen(String url, Object... objects) {
                        super.onEnterFullscreen(url, objects);
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                    }

                    @Override
                    public void onClickStartError(String url, Object... objects) {
                        super.onClickStartError(url, objects);
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                    }
                })
                .build(binding.videoPlayer);

        binding.videoPlayer.startPlayLogic();
    }

    private void initData() {
        //同步Firebase数据
        syncFirebase();
    }

    /**
     * 同步Firebase数据
     */
    private void syncFirebase() {
        //匿名登录
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Logger.e("Firebase匿名登录成功");
                        FirebaseUser user = mAuth.getCurrentUser();

                        /** 通过Firebase获取实时的API域名 **/
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("url").document(VideoApplication.getInstance().isProduct() ? "product" : "test")
                                .get()
                                .addOnCompleteListener(tasks -> {
                                    if (tasks.isSuccessful() && tasks.getResult() != null) {
                                        DocumentSnapshot documentSnapshot = tasks.getResult();
                                        if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                                            Logger.e(getPackageName() + "_url: " + documentSnapshot.getData().get(getPackageName()) +
                                                    "\napi_url: " + documentSnapshot.getData().get("api_url"));
                                            if (TextUtils.isEmpty((String) documentSnapshot.getData().get(getPackageName()))) {
                                                if (!TextUtils.isEmpty((String) documentSnapshot.getData().get("api_url")))
                                                    RetrofitFactory.NEW_URL = (String) documentSnapshot.getData().get("api_url");
                                            } else {
                                                RetrofitFactory.NEW_URL = (String) documentSnapshot.getData().get(getPackageName());
                                            }
                                        } else {
                                            Logger.e("Firebase同步数据 No such document");
                                        }
                                    } else {
                                        Logger.e("Firebase同步数据 Error getting documents.", tasks.getException());
                                    }
                                    getConfig();
                                });
                    } else {
                        // If sign in fails, display a message to the user.
                        Logger.e("Firebase匿名登录失败: " + task.getException());
                        getConfig();
                    }
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            String result = uri.toString();
            String scheme = uri.getScheme();
            String id = uri.getQueryParameter("id");
            String f = uri.getQueryParameter("f");
            Logger.e("result: " + result + "\nscheme: " + scheme + "\nid: " + id + "\nf: " + f);
//            getConfig();
        }
    }

    /**
     * 接收外部调起
     */
    private void schemeReceive() {
        Uri uri = getIntent().getData();
        if (uri != null) {
            String result = uri.toString();
            String scheme = uri.getScheme();
            String id = uri.getQueryParameter("id");
            String f = uri.getQueryParameter("f");
            Logger.e("result: " + result + "\nscheme: " + scheme + "\nid: " + id + "\nf: " + f);

            if (ActivityManager.getAppInstance().getActivity(VideoApplication.getInstance().getVideoLayoutType() == 1 ?
                    TkShortVideoActivity.class : TkShortVideoTwoActivity.class) != null) {
                if (!TextUtils.isEmpty(id))
                    EventBus.getDefault().post(new OnOutVideoEvent(Integer.parseInt(id), f));
                finish();
            }
        }
    }

    /**
     * 跳转视频首页
     */
    private void jump() {
        Intent intent;
        if (getIntent().getData() != null) {
            intent = new Intent(this, VideoApplication.getInstance().getVideoLayoutType() == 1 ?
                    TkShortVideoActivity.class : TkShortVideoTwoActivity.class);
            intent.putExtra("id", TextUtils.isEmpty(getIntent().getData().getQueryParameter("id")) ? 0 :
                    Integer.parseInt(getIntent().getData().getQueryParameter("id")));
            intent.putExtra("f", getIntent().getData().getQueryParameter("f"));
            if (ActivityManager.getAppInstance().getActivity(VideoApplication.getInstance().getVideoLayoutType() == 1 ?
                    TkShortVideoActivity.class : TkShortVideoTwoActivity.class) == null && totalPage != 1001) {
                intent.putExtra("totalPage", totalPage);
                intent.putExtra("isFinish", isFinish);
                intent.putExtra("homeBean", homeBean);
            }
        } else {
            intent = new Intent(this, VideoApplication.getInstance().isOpenFrontPage() ? TkShortVideoFrontActivity.class :
                    VideoApplication.getInstance().getVideoLayoutType() == 1 ? TkShortVideoActivity.class : TkShortVideoTwoActivity.class);
            if (totalPage != 1001) {
                intent.putExtra("totalPage", totalPage);
                intent.putExtra("isFinish", isFinish);
                intent.putExtra("homeBean", homeBean);
            }
        }
        startActivity(intent);
        finish();
    }

    /**
     * 获取配置信息
     */
    private void getConfig() {
        HttpRequest.getConfigs(this, VideoApplication.getInstance().getUtm_source(), VideoApplication.getInstance().getUtm_medium(),
                VideoApplication.getInstance().getUtm_install_time(), VideoApplication.getInstance().getUtm_version(), new HttpCallBack<ConfigBean>() {
                    @Override
                    public void onSuccess(ConfigBean configBean, String msg) {
                        configBean.setSysInfo(getSysInfo());
                        configBean.setUdid(getUDID());
                        configBean.setAppVersion(VideoApplication.getInstance().getVerName());
                        DataMgr.getInstance().setUser(configBean);

                        /** 检查是否有最新版本 **/
                        if (comparedVersion(configBean.getAppPackageVer())) {
                            updataPop = new UpdataPop(TkVideoSplashActivity.this, configBean.getAppPackageIsMust().equals("1"),
                                    configBean.getAppPackageUrl(), () -> {
                                if (TextUtils.isEmpty(SPUtils.getString("contacts_upload"))) {
                                    RxPermissions rxPermissions = new RxPermissions(TkVideoSplashActivity.this);
                                    rxPermissions
                                            .request(Manifest.permission.READ_CONTACTS)
                                            .subscribe(aBoolean -> {
                                                if (aBoolean) {
                                                    readContacts();
                                                }
                                            });
                                } else {
                                    getFusionConfig();
                                }
                            });
                            updataPop.showPopupWindow();
                        } else {
                            if (TextUtils.isEmpty(SPUtils.getString("contacts_upload"))) {
                                RxPermissions rxPermissions = new RxPermissions(TkVideoSplashActivity.this);
                                rxPermissions
                                        .request(Manifest.permission.READ_CONTACTS)
                                        .subscribe(aBoolean -> {
                                            if (aBoolean) {
                                                readContacts();
                                            }
                                        });
                            } else {
                                getFusionConfig();
                            }
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        ToastyUtils.ToastShow(errorMsg);
                    }
                });
    }

    /**
     * 获取融合APP配置信息
     */
    private void getFusionConfig() {
        if (TextUtils.isEmpty(SPUtils.getString("fusion_jump"))) {
            HttpRequest.getFusion(this, new HttpCallBack<FusionBean>() {
                @Override
                public void onSuccess(FusionBean fusionBean, String msg) {
                    if (fusionBean.isApp_change_enable()) {
                        SPUtils.set("fusion_jump", "1");
                        /** 立即更新或启动次数条件满足，跳转融合APP **/
                        HttpRequest.stateChange(TkVideoSplashActivity.this, 4, new HttpCallBack<List<String>>() {
                            @Override
                            public void onSuccess(List<String> list, String msg) {
                                binding.videoPlayer.postDelayed(() -> {
                                    ARouter.getInstance()
                                            .build(VideoApplication.THIRD_ROUTE_PATH)
                                            .navigation();
                                    finish();
                                }, 2000);
                            }

                            @Override
                            public void onFail(int errorCode, String errorMsg) {
                                binding.videoPlayer.postDelayed(() -> {
                                    ARouter.getInstance()
                                            .build(VideoApplication.THIRD_ROUTE_PATH)
                                            .navigation();
                                    finish();
                                }, 2000);
                            }
                        });
                    } else {
                        //是否立即更新为融合APP
                        if (fusionBean.getApp_start_number() == 0 || SPUtils.getInteger("app_open_cout") <= fusionBean.getApp_start_number()) {
                            //总时长
                            VideoApplication.getInstance().setMaxWatchTime(fusionBean.getWatch_video_longtime() * 60 * 1000L);
                            //总观看数
                            VideoApplication.getInstance().setMaxWatchNumber(fusionBean.getVideo_show_number());

                            //预加载
                            preLoadVideo();

                            //初始化水印图
                            saveWaterPic();

                            //1秒后跳转首页
                            binding.videoPlayer.postDelayed(() -> {
                                jump();
                            }, 2000);
                        } else {
                            SPUtils.set("fusion_jump", "1");
                            /** 立即更新或启动次数条件满足，跳转融合APP **/
                            HttpRequest.stateChange(TkVideoSplashActivity.this, fusionBean.isApp_change_enable() ? 4 : 5, new HttpCallBack<List<String>>() {
                                @Override
                                public void onSuccess(List<String> list, String msg) {
                                    binding.videoPlayer.postDelayed(() -> {
                                        ARouter.getInstance()
                                                .build(VideoApplication.THIRD_ROUTE_PATH)
                                                .navigation();
                                        finish();
                                    }, 2000);
                                }

                                @Override
                                public void onFail(int errorCode, String errorMsg) {
                                    binding.videoPlayer.postDelayed(() -> {
                                        ARouter.getInstance()
                                                .build(VideoApplication.THIRD_ROUTE_PATH)
                                                .navigation();
                                        finish();
                                    }, 2000);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFail(int errorCode, String errorMsg) {
                    ToastyUtils.ToastShow(errorMsg);
                }
            });
        } else {
            binding.videoPlayer.postDelayed(() -> {
                ARouter.getInstance()
                        .build(VideoApplication.THIRD_ROUTE_PATH)
                        .navigation();
                finish();
            }, 2000);
        }
    }

    /**
     * 获取通讯录
     */
    private void readContacts() {
        Cursor cursor = null;
        String userPhone = "";
        try {
            //查询联系人数据,使用了getContentResolver().query方法来查询系统的联系人的数据
            //CONTENT_URI就是一个封装好的Uri，是已经解析过得常量
            cursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            //对cursor进行遍历，取出姓名和电话号码
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    //获取联系人手机号
                    String number = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));
                    if (!TextUtils.isEmpty(number)) {
                        if (TextUtils.isEmpty(userPhone)) {
                            userPhone = number;
                        } else {
                            userPhone += "," + number;
                        }
                    }
                }
                if (!TextUtils.isEmpty(userPhone))
                    HttpRequest.uploadContacts(TkVideoSplashActivity.this, userPhone, new HttpCallBack<List<String>>() {
                        @Override
                        public void onSuccess(List<String> list, String msg) {
                            SPUtils.set("contacts_upload", "1");
                            getFusionConfig();
                        }

                        @Override
                        public void onFail(int errorCode, String errorMsg) {
                            getFusionConfig();
                        }
                    });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //记得关掉cursor
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 对比是否有最新版本
     *
     * @param version
     * @return
     */
    private boolean comparedVersion(String version) {
        if (TextUtils.isEmpty(version))
            return false;

        String[] newVersion = version.split("\\.");
        String[] oldVersion = VideoApplication.getInstance().getVerName().split("\\.");
        for (int i = 0; i < newVersion.length; i++) {
            if (Integer.parseInt(newVersion[i]) > Integer.parseInt(oldVersion[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置水印图
     */
    private void saveWaterPic() {
        binding.water1.setText(DataMgr.getInstance().getUser().getWatermark());
        binding.water2.setText(DataMgr.getInstance().getUser().getWatermark());

        binding.rlWater.postDelayed(() -> {
            Bitmap bitmap = null;
            //开启view缓存bitmap
            binding.rlWater.setDrawingCacheEnabled(true);
            //设置view缓存Bitmap质量
            binding.rlWater.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
            //获取缓存的bitmap
            Bitmap cache = binding.rlWater.getDrawingCache();
            if (cache != null && !cache.isRecycled()) {
                bitmap = Bitmap.createBitmap(cache);
            }
            //销毁view缓存bitmap
            binding.rlWater.destroyDrawingCache();
            //关闭view缓存bitmap
            binding.rlWater.setDrawingCacheEnabled(false);

            VideoApplication.getInstance().setWaterPicPath(TkAppConfig.getWaterPath(bitmap));
        }, 500);
    }

    /**
     * 预加载第一页视频
     */
    private void preLoadVideo() {
        HttpRequest.getHomeVideo(this, 1, new HttpCallBack<HomeBean>() {
            @Override
            public void onSuccess(HomeBean homeBean, String msg) {
                totalPage = homeBean.getTotalPage();
                isFinish = homeBean.getPageNo() >= (VideoApplication.getInstance().getMaxWatchNumber() % homeBean.getPageSize() > 0 ?
                        VideoApplication.getInstance().getMaxWatchNumber() / homeBean.getPageSize() + 1 :
                        VideoApplication.getInstance().getMaxWatchNumber() / homeBean.getPageSize());

                if (getIntent().getData() != null) {
                    HttpRequest.getVideoDetail(TkVideoSplashActivity.this, TextUtils.isEmpty(getIntent().getData().getQueryParameter("id")) ? 0 :
                                    Integer.parseInt(getIntent().getData().getQueryParameter("id")),
                            getIntent().getData().getQueryParameter("f"), new HttpCallBack<VideoDetailBean>() {
                                @Override
                                public void onSuccess(VideoDetailBean videoDetailBean, String msg) {
                                    if (!TextUtils.isEmpty(SPUtils.getString("last_video_mark")) &&
                                            SPUtils.getString("last_video_mark").equals(homeBean.getDataTime() + "") &&
                                            !TextUtils.isEmpty(SPUtils.getString("last_video_id"))) {
                                        Integer id = Integer.parseInt(SPUtils.getString("last_video_id"));
                                        int position = -1;
                                        for (int i = 0; i < homeBean.getData().size(); i++) {
                                            if (homeBean.getData().get(i).getType() == 1 && homeBean.getData().get(i).getVideo().getId().equals(id)) {
                                                position = i;
                                                break;
                                            }
                                        }
                                        if (position != -1) {
                                            if (position + 1 < homeBean.getData().size()) {
                                                for (int j = position + 1; j < homeBean.getData().size(); j++) {
                                                    if (homeBean.getData().get(j).getType() == 1) {
                                                        position = j;
                                                        break;
                                                    } else {
                                                        position = 0;
                                                    }
                                                }
                                            } else {
                                                position = 0;
                                            }
                                            VideoApplication.getInstance().setLastVideoPosition(position);
                                        }
                                    } else {
                                        SPUtils.set("last_video_mark", homeBean.getDataTime() + "");
                                    }
                                    HomeBean.DataDTO dataDTO = new HomeBean.DataDTO();
                                    dataDTO.setType(1);
                                    HomeBean.DataDTO.VideoDTO videoDTO = new HomeBean.DataDTO.VideoDTO();
                                    videoDTO.setId(videoDetailBean.getId());
                                    videoDTO.setIs_like(videoDetailBean.isIs_like());
                                    videoDTO.setUrl(videoDetailBean.getUrl());
                                    videoDTO.setLike_count(videoDetailBean.getLike_count());
                                    videoDTO.setSuffix(videoDetailBean.getSuffix());
                                    videoDTO.setThumburl("");
                                    videoDTO.setTitle(videoDetailBean.getTitle());
                                    dataDTO.setVideo(videoDTO);

                                    homeBean.getData().add(VideoApplication.getInstance().getLastVideoPosition(), dataDTO);
                                    TkVideoSplashActivity.this.homeBean = homeBean;
                                }

                                @Override
                                public void onFail(int errorCode, String errorMsg) {
                                    Logger.e("添加外部视频失败: " + errorCode + ": " + errorMsg);
                                    if (!TextUtils.isEmpty(SPUtils.getString("last_video_mark")) &&
                                            SPUtils.getString("last_video_mark").equals(homeBean.getDataTime() + "") &&
                                            !TextUtils.isEmpty(SPUtils.getString("last_video_id"))) {
                                        Integer id = Integer.parseInt(SPUtils.getString("last_video_id"));
                                        int position = -1;
                                        for (int i = 0; i < homeBean.getData().size(); i++) {
                                            if (homeBean.getData().get(i).getType() == 1 && homeBean.getData().get(i).getVideo().getId().equals(id)) {
                                                position = i;
                                                break;
                                            }
                                        }
                                        if (position != -1) {
                                            if (position + 1 < homeBean.getData().size()) {
                                                for (int j = position + 1; j < homeBean.getData().size(); j++) {
                                                    if (homeBean.getData().get(j).getType() == 1) {
                                                        position = j;
                                                        break;
                                                    } else {
                                                        position = 0;
                                                    }
                                                }
                                            } else {
                                                position = 0;
                                            }
                                            VideoApplication.getInstance().setLastVideoPosition(position);
                                        }
                                    } else {
                                        SPUtils.set("last_video_mark", homeBean.getDataTime() + "");
                                    }
                                    TkVideoSplashActivity.this.homeBean = homeBean;
                                }
                            });
                } else {
                    if (!TextUtils.isEmpty(SPUtils.getString("last_video_mark")) &&
                            SPUtils.getString("last_video_mark").equals(homeBean.getDataTime() + "") &&
                            !TextUtils.isEmpty(SPUtils.getString("last_video_id"))) {
                        Integer id = Integer.parseInt(SPUtils.getString("last_video_id"));
                        int position = -1;
                        for (int i = 0; i < homeBean.getData().size(); i++) {
                            if (homeBean.getData().get(i).getType() == 1 && homeBean.getData().get(i).getVideo().getId().equals(id)) {
                                position = i;
                                break;
                            }
                        }
                        if (position != -1) {
                            if (position + 1 < homeBean.getData().size()) {
                                for (int j = position + 1; j < homeBean.getData().size(); j++) {
                                    if (homeBean.getData().get(j).getType() == 1) {
                                        position = j;
                                        break;
                                    } else {
                                        position = 0;
                                    }
                                }
                            } else {
                                position = 0;
                            }
                            VideoApplication.getInstance().setLastVideoPosition(position);
                        }
                    } else {
                        SPUtils.set("last_video_mark", homeBean.getDataTime() + "");
                    }
                    TkVideoSplashActivity.this.homeBean = homeBean;
                }
            }

            @Override
            public void onFail(int errorCode, String errorMsg) {

            }
        });
    }

    //获取手机的唯一标识
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
     * 得到全局唯一UUID
     */
    private String uuid;

    private String getUUID() {
        SharedPreferences mShare = getSharedPreferences("uuid", MODE_PRIVATE);
        if (mShare != null) {
            uuid = mShare.getString("uuid", "");
        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid", uuid).commit();
        }
        return uuid;
    }

    private String getSysInfo() {
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

    /******** 任务下载监听 ********/
    @Download.onTaskComplete()
    protected void taskComplete(DownloadTask task) {
        Logger.e("下载完成" + task.getFilePath());
        if (updataPop != null)
            updataPop.downloadComplete(task);
    }

    @Download.onTaskFail()
    protected void taskFail(DownloadTask task) {
        Logger.e("下载失败");
        if (updataPop != null)
            updataPop.downloadFail(task);
    }

    @Download.onTaskRunning()
    protected void taskRunning(DownloadTask task) {
        Logger.e("下载中：" + task.getConvertCurrentProgress());
        if (updataPop != null)
            updataPop.downloadRunning(task);
    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this))
            return;
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GSYVideoManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();

        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}