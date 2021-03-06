package com.shortvideo.lib.ui.activity;

import static android.view.View.DRAWING_CACHE_QUALITY_HIGH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.gson.Gson;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.logger.Logger;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.TkAppConfig;
import com.shortvideo.lib.common.event.OnOutVideoEvent;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.databinding.TkActivityVideoSplashBinding;
import com.shortvideo.lib.model.ConfigBean;
import com.shortvideo.lib.model.DataMgr;
import com.shortvideo.lib.model.HomeBean;
import com.shortvideo.lib.model.VideoDetailBean;
import com.shortvideo.lib.ui.activity.concise.ncindex.NcIndexActivity;
import com.shortvideo.lib.ui.activity.front.TkShortVideoFrontActivity;
import com.shortvideo.lib.ui.widgets.UpdataPop;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.SPUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;
import java.util.UUID;

public class TkVideoSplashActivity extends AppCompatActivity {

    TkActivityVideoSplashBinding binding;

    private boolean isFinish = false; //??????????????????
    private int totalPage = 1001;  //????????????
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
        if (TextUtils.isEmpty(SPUtils.getString("fusion_get_configs"))) {
            HttpRequest.getConfigs(this, VideoApplication.getInstance().getUtm_source(), VideoApplication.getInstance().getUtm_medium(),
                    VideoApplication.getInstance().getUtm_install_time(), VideoApplication.getInstance().getUtm_version(),
                    new HttpCallBack<ConfigBean>() {
                        @Override
                        public void onSuccess(ConfigBean configBean, String msg) {
                            configBean.setSysInfo(VideoApplication.getInstance().getSysInfo());
                            configBean.setUdid(VideoApplication.getInstance().getUDID());
                            configBean.setAppVersion(VideoApplication.getInstance().getVerName());
                            DataMgr.getInstance().setUser(configBean);

                            SPUtils.set("fusion_get_configs", new Gson().toJson(configBean));

                            //?????????
                            preLoadVideo();

                            //??????????????????
                            saveWaterPic();

                            //1??????????????????
                            binding.videoPlayer.postDelayed(TkVideoSplashActivity.this::jump, 2000);
                        }

                        @Override
                        public void onFail(int errorCode, String errorMsg) {
                            initData();
                        }
                    });
        } else {
            //?????????
            preLoadVideo();

            //??????????????????
            saveWaterPic();

            //1??????????????????
            binding.videoPlayer.postDelayed(TkVideoSplashActivity.this::jump, 2000);
        }
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
     * ??????????????????
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
     * ??????????????????
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
            intent = new Intent(this, VideoApplication.getInstance().getOpenPageWhere() == 1 ?
                    NcIndexActivity.class : VideoApplication.getInstance().getOpenPageWhere() == 2 ? TkShortVideoFrontActivity.class :
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
     * ???????????????????????????
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
     * ???????????????
     */
    private void saveWaterPic() {
        binding.water1.setText(DataMgr.getInstance().getUser().getWatermark());
        binding.water2.setText(DataMgr.getInstance().getUser().getWatermark());

        binding.rlWater.postDelayed(() -> {
            Bitmap bitmap = null;
            //??????view??????bitmap
            binding.rlWater.setDrawingCacheEnabled(true);
            //??????view??????Bitmap??????
            binding.rlWater.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
            //???????????????bitmap
            Bitmap cache = binding.rlWater.getDrawingCache();
            if (cache != null && !cache.isRecycled()) {
                bitmap = Bitmap.createBitmap(cache);
            }
            //??????view??????bitmap
            binding.rlWater.destroyDrawingCache();
            //??????view??????bitmap
            binding.rlWater.setDrawingCacheEnabled(false);

            VideoApplication.getInstance().setWaterPicPath(TkAppConfig.getWaterPath(bitmap));
        }, 500);
    }

    /**
     * ????????????????????????
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
                                    Logger.e("????????????????????????: " + errorCode + ": " + errorMsg);
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

    /******** ?????????????????? ********/
    @Download.onTaskComplete()
    protected void taskComplete(DownloadTask task) {
        Logger.e("????????????" + task.getFilePath());
        if (updataPop != null)
            updataPop.downloadComplete(task);
    }

    @Download.onTaskFail()
    protected void taskFail(DownloadTask task) {
        Logger.e("????????????");
        if (updataPop != null)
            updataPop.downloadFail(task);
    }

    @Download.onTaskRunning()
    protected void taskRunning(DownloadTask task) {
        Logger.e("????????????" + task.getConvertCurrentProgress());
        if (updataPop != null)
            updataPop.downloadRunning(task);
    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this))
            return;
//        super.onBackPressed();
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