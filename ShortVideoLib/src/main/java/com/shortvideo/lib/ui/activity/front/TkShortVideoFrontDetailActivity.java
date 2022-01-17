package com.shortvideo.lib.ui.activity.front;

import static android.view.View.DRAWING_CACHE_QUALITY_HIGH;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.logger.Logger;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.TkAppConfig;
import com.shortvideo.lib.common.event.OnVideoDislikeEvent;
import com.shortvideo.lib.common.event.OnVideoDoubleLikeEvent;
import com.shortvideo.lib.common.event.OnVideoLongPressEvent;
import com.shortvideo.lib.common.event.OnVideoReportEvent;
import com.shortvideo.lib.common.event.OnWaterEmptyEvent;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.databinding.TkActivityShortVideoFrontDetailBinding;
import com.shortvideo.lib.model.AdBean;
import com.shortvideo.lib.model.DataMgr;
import com.shortvideo.lib.model.HomeBean;
import com.shortvideo.lib.ui.widgets.DownloadPop;
import com.shortvideo.lib.ui.widgets.SharePop;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.ClickUtil;
import com.shortvideo.lib.utils.SPUtils;
import com.shortvideo.lib.utils.ToastyUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TkShortVideoFrontDetailActivity extends AppCompatActivity implements View.OnClickListener {

    TkActivityShortVideoFrontDetailBinding binding;

    private int type;
    private int position;
    private HomeBean.DataDTO.VideoDTO videoBean;
    private HomeBean.DataDTO.BannerDTO bannerBean;

    private DownloadPop downloadPop;
    private SharePop sharePop;

    private Timer adCardTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                .statusBarDarkFont(false, 0f)
                .init();

        binding = TkActivityShortVideoFrontDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        EventBus.getDefault().register(this);

        Aria.download(this).register();

        type = getIntent().getIntExtra("type", 1);
        position = getIntent().getIntExtra("position", -1);
        videoBean = (HomeBean.DataDTO.VideoDTO) getIntent().getSerializableExtra("videoBean");
        bannerBean = (HomeBean.DataDTO.BannerDTO) getIntent().getSerializableExtra("bannerBean");

        reportBannerShow(type == 1 ? videoBean.getId() : bannerBean != null ? bannerBean.getId() : 0, type);
    }

    private void initData() {
        if (type != 2) {
            binding.videoplayer.setLink("");
            binding.videoplayer.setPosition(position);
            binding.videoplayer.setBefore(videoBean.getThumburl());
            binding.like.setImageResource(videoBean.isIs_like() ? R.mipmap.tk_icon_liked : R.mipmap.tk_icon_like);
            binding.like.setVisibility(VideoApplication.getInstance().isApplyToLike() ? (!videoBean.isPureEnjoyment() ?
                    View.VISIBLE : View.INVISIBLE) : View.GONE);
            binding.likeNum.setText(videoBean.getLike_count() + "");
            binding.likeNum.setVisibility(VideoApplication.getInstance().isApplyToLike() ? (videoBean.getLike_count() > 0 && !videoBean.isPureEnjoyment() ?
                    View.VISIBLE : View.INVISIBLE) : View.GONE);
            binding.share.setVisibility(!videoBean.isPureEnjoyment() ? View.VISIBLE : View.INVISIBLE);
            binding.download.setVisibility(VideoApplication.getInstance().isApplyDownload() ? (!videoBean.isPureEnjoyment() ?
                    View.VISIBLE : View.INVISIBLE) : View.GONE);
            binding.llTitle.setVisibility(!videoBean.isPureEnjoyment() ? View.VISIBLE : View.INVISIBLE);
            binding.title.setText(videoBean.getTitle());
            binding.aut.setText(videoBean.getAuthor());
            binding.aut.setVisibility(TextUtils.isEmpty(videoBean.getAuthor()) ? View.GONE : View.VISIBLE);
            binding.content.setText(videoBean.getDesc());
            binding.content.setVisibility(TextUtils.isEmpty(videoBean.getDesc()) ? View.GONE : View.VISIBLE);
            binding.adImage.setVisibility(View.GONE);
            binding.videoplayer.setVisibility(View.VISIBLE);
            binding.llBtn.setVisibility(View.VISIBLE);
            binding.llExt.setVisibility(View.GONE);
            binding.llExtBig.setVisibility(View.GONE);
            binding.eyes.setVisibility(!VideoApplication.getInstance().isPureEnjoyment() ? View.GONE : View.VISIBLE);

            //纯享模式
            if (VideoApplication.getInstance().isPureEnjoyment()) {
                if (!videoBean.isPureEnjoyment()) {
                    binding.eyes.playAnimation();
                } else {
                    binding.eyes.cancelAnimation();
                    binding.eyes.setProgress(0f);
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
                    .setUrl(videoBean.getUrl())
                    .setCacheWithPlay(true)
                    .setVideoAllCallBack(new GSYSampleCallBack() {

                        @Override
                        public void onStartPrepared(String url, Object... objects) {
                            super.onStartPrepared(url, objects);
                        }

                        @Override
                        public void onPrepared(String url, Object... objects) {
                            super.onPrepared(url, objects);
                            //设置 seek 的临近帧。
//                            jzvdStd.postDelayed(() -> {
//                                if (jzvdStd.getGSYVideoManager().getPlayer() instanceof Exo2PlayerManager &&
//                                        ((Exo2PlayerManager) jzvdStd.getGSYVideoManager().getPlayer()) != null)
//                                    ((Exo2PlayerManager) jzvdStd.getGSYVideoManager().getPlayer()).setSeekParameter(SeekParameters.NEXT_SYNC);
//                            }, 100);
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
                    .setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> {

                        if (binding.videoplayer.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING)
                            binding.videoplayer.setProgresses(progress);
                    })
                    .build(binding.videoplayer);

            binding.videoplayer.startPlayLogic();
        } else {
            adCardTimer = new Timer();
            adCardTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    message.arg1 = position;
                    handler.sendMessage(message);
                }
            }, 5000);

            binding.title.setText(bannerBean.getTitle());
            binding.adDec.setText(bannerBean.getDesc());
            binding.adDec.setVisibility(TextUtils.isEmpty(bannerBean.getDesc()) ? View.GONE : View.VISIBLE);
            binding.adTitle.setText(bannerBean.getTitle());
            binding.adContent.setText(bannerBean.getLink());
            binding.smallAdTitle.setText(bannerBean.getTitle());
            binding.smallAdContent.setText(bannerBean.getLink());
            binding.videoplayer.setVisibility(View.GONE);
            binding.adTip.setVisibility(!bannerBean.isShow() ? View.GONE : View.VISIBLE);
            binding.adImage.setVisibility(View.GONE);
            binding.llBtn.setVisibility(View.GONE);
            binding.llTitle.setVisibility(View.GONE);
            binding.llExt.setVisibility(View.VISIBLE);
            binding.llExtBig.setVisibility(View.GONE);
            binding.eyes.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(bannerBean.getIcon())) {
                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                Glide.with(this)
                        .load(bannerBean.getIcon())
                        .dontAnimate()
                        .apply(options)
                        .into(binding.adImg);

                Glide.with(this)
                        .load(bannerBean.getIcon())
                        .dontAnimate()
                        .apply(options)
                        .into(binding.smallAdImg);
            }

            binding.videoplayer.setBefore("");
            binding.videoplayer.setLink(bannerBean.getLink());
            binding.videoplayer.setBannerId(bannerBean.getId(), bannerBean.getBanner_type());

            binding.videoplayer.setOnClickListener(v -> {
                Uri content_url = Uri.parse(bannerBean.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
                startActivity(intent);
                binding.videoplayer.reportBannerClick(bannerBean.getId(), 2);
            });

            binding.adImage.setOnClickListener(v -> {
                Uri content_url = Uri.parse(bannerBean.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
                startActivity(intent);
                binding.videoplayer.reportBannerClick(bannerBean.getId(), 1);
            });

            if (bannerBean.getBanner_type() == 2) {
                binding.adImage.setVisibility(View.VISIBLE);
                binding.videoplayer.setVisibility(View.GONE);

                binding.videoplayer.release();

                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                Glide.with(this)
                        .load(bannerBean.getUrl())
                        .dontAnimate()
                        .apply(options)
                        .into(binding.adImage);
            } else {
                binding.adImage.setVisibility(View.GONE);
                binding.videoplayer.setVisibility(View.VISIBLE);
                binding.videoplayer.setBefore("");

                GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
                gsyVideoOption.setIsTouchWiget(false)
                        .setRotateViewAuto(false)
                        .setRotateWithSystem(false)
                        .setLockLand(false)
                        .setAutoFullWithSize(true)
                        .setShowFullAnimation(false)
                        .setLooping(true)
                        .setNeedLockFull(false)
                        .setUrl(bannerBean.getUrl())
                        .setCacheWithPlay(true)
                        .setVideoAllCallBack(new GSYSampleCallBack() {
                            @Override
                            public void onPrepared(String url, Object... objects) {
                                super.onPrepared(url, objects);

                                //设置 seek 的临近帧。
//                                jzvdStd.postDelayed(() -> {
//                                    if (jzvdStd.getGSYVideoManager().getPlayer() instanceof Exo2PlayerManager)
//                                        ((Exo2PlayerManager) jzvdStd.getGSYVideoManager().getPlayer()).setSeekParameter(SeekParameters.NEXT_SYNC);
//                                }, 100);
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
//                            .setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> Logger.e("播放进度: " + progress + " / " + secProgress + " / " + currentPosition))
                        .build(binding.videoplayer);

                binding.videoplayer.startPlayLogic();
            }
        }
    }

    private void initListener() {
        binding.llTitle.setOnClickListener(this);
        binding.share.setOnClickListener(this);
        binding.like.setOnClickListener(this);
        binding.download.setOnClickListener(this);
        binding.llTip.setOnClickListener(this);
        binding.adClose.setOnClickListener(this);
        binding.adBtn.setOnClickListener(this);
        binding.smallAdBtn.setOnClickListener(this);
        binding.llExt.setOnClickListener(this);
        binding.llExtBig.setOnClickListener(this);
        binding.eyes.setOnClickListener(this);
        binding.back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (ClickUtil.isFastClick()) return;

        if (view.getId() == R.id.ll_title) {
            //无效点击，屏蔽点赞
        } else if (view.getId() == R.id.share) {
            sharePop = new SharePop(this, videoBean.getId());
            downloadPop = new DownloadPop(this, videoBean.getId(), position, sharePop);
            downloadPop.showPopupWindow();
        } else if (view.getId() == R.id.like) {
            if (videoBean.isIs_like()) {
                cancelLike(videoBean.getId(), position);
            } else {
                goLike(videoBean.getId(), position);
            }
        } else if (view.getId() == R.id.download) {
            sharePop = new SharePop(this, videoBean.getId());
            downloadPop = new DownloadPop(this, videoBean.getId(), position, sharePop);
            downloadPop.openDownload();
        } else if (view.getId() == R.id.ll_tip) {
            bannerBean.setShow(!bannerBean.isShow());
            binding.adTip.setVisibility(bannerBean.isShow() ? View.VISIBLE : View.GONE);
        } else if (view.getId() == R.id.ad_close) {
            binding.llExtBig.setVisibility(View.GONE);
        } else if (view.getId() == R.id.ad_btn || view.getId() == R.id.small_ad_btn || view.getId() == R.id.ll_ext ||
                view.getId() == R.id.ll_ext_big) {
            Uri content_url = Uri.parse(bannerBean.getLink());
            Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
            startActivity(intent);
            binding.videoplayer.reportBannerClick(bannerBean.getId(), 3);
        } else if (view.getId() == R.id.eyes) {
            if (type == 1) {
                if (videoBean.isPureEnjoyment()) {
                    videoBean.setPureEnjoyment(false);
                    if (VideoApplication.getInstance().isApplyToLike()) {
                        binding.like.setVisibility(View.VISIBLE);
                        binding.likeNum.setVisibility(videoBean.getLike_count() > 0 ? View.VISIBLE : View.INVISIBLE);
                    }
                    binding.share.setVisibility(View.VISIBLE);
                    if (VideoApplication.getInstance().isApplyDownload())
                        binding.download.setVisibility(View.VISIBLE);
                    binding.llTitle.setVisibility(View.VISIBLE);
                    binding.videoplayer.setProgressVisible(true);
                    binding.eyes.playAnimation();
                } else {
                    videoBean.setPureEnjoyment(true);
                    if (VideoApplication.getInstance().isApplyToLike()) {
                        binding.like.setVisibility(View.INVISIBLE);
                        binding.likeNum.setVisibility(View.INVISIBLE);
                    }
                    binding.share.setVisibility(View.INVISIBLE);
                    if (VideoApplication.getInstance().isApplyDownload())
                        binding.download.setVisibility(View.INVISIBLE);
                    binding.llTitle.setVisibility(View.INVISIBLE);
                    binding.videoplayer.setProgressVisible(false);
                    binding.eyes.cancelAnimation();
                    binding.eyes.setProgress(0f);
                }
            }
        } else if (view.getId() == R.id.back) {
            finish();
        }
    }

    private final Handler handler = new Handler(msg -> {
        if (msg.what == 1) {
            binding.llExt.setVisibility(View.GONE);
            binding.llExtBig.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(TkShortVideoFrontDetailActivity.this, R.anim.tk_ad_translate);
            binding.llExtBig.setAnimation(animation);
            binding.llExtBig.startAnimation(animation);
        }
        return false;
    });

    /**
     * 视频双击事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoDoubleLike(OnVideoDoubleLikeEvent event) {
        if (event != null)
            if (!videoBean.isIs_like())
                goLike(videoBean.getId(), event.getPosition());
    }

    /**
     * 视频长按
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoLongPress(OnVideoLongPressEvent event) {
        if (event != null) {
            sharePop = new SharePop(this, videoBean.getId());
            downloadPop = new DownloadPop(this, videoBean.getId(), position, sharePop);
            downloadPop.showPopupWindow();
        }
    }

    /**
     * 不喜欢该视频
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoDislike(OnVideoDislikeEvent event) {
        if (event != null) {
            finish();
        }
    }

    /**
     * 举报视频成功
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoReport(OnVideoReportEvent event) {
        if (event != null) {
            finish();
        }
    }

    /**
     * 获取水印图为空事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void waterEmpty(OnWaterEmptyEvent event) {
        if (event != null) {
            binding.rlWater.setVisibility(View.VISIBLE);
            saveWaterPic();
        }
    }

    /**
     * 设置水印图
     */
    private void saveWaterPic() {
        binding.water1.setText(DataMgr.getInstance().getUser().getWatermark());
        binding.water2.setText(DataMgr.getInstance().getUser().getWatermark());

        //view转bitmap
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
            binding.rlWater.setVisibility(View.GONE);
        }, 500);
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
        if (adCardTimer != null)
            adCardTimer.cancel();
        GSYVideoManager.releaseAllVideos();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    /**
     * 点赞
     *
     * @param vid
     * @param position
     */
    private void goLike(int vid, int position) {
        HttpRequest.goLike(this, vid, new HttpCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> s, String msg) {
                ToastyUtils.ToastShow(getString(R.string.tk_like_success));

                binding.like.setImageResource(R.mipmap.tk_icon_liked);
                binding.likeNum.setText((videoBean.getLike_count() + 1) + "");
                binding.likeNum.setVisibility(View.VISIBLE);
                videoBean.setIs_like(true);
                videoBean.setLike_count(videoBean.getLike_count() + 1);
            }

            @Override
            public void onFail(int errorCode, String errorMsg) {
                ToastyUtils.ToastShow(errorMsg);
            }
        });
    }

    /**
     * 取消点赞
     *
     * @param vid
     * @param position
     */
    private void cancelLike(int vid, int position) {
        HttpRequest.cancelLike(this, vid, new HttpCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> s, String msg) {
                ToastyUtils.ToastShow(getString(R.string.tk_unlike_success));

                binding.like.setImageResource(R.mipmap.tk_icon_like);
                binding.likeNum.setText(Math.max(videoBean.getLike_count() - 1, 0) + "");
                binding.likeNum.setVisibility(Math.max(videoBean.getLike_count() - 1, 0) > 0 ? View.VISIBLE : View.INVISIBLE);
                videoBean.setIs_like(false);
                videoBean.setLike_count(Math.max(videoBean.getLike_count() - 1, 0));
            }

            @Override
            public void onFail(int errorCode, String errorMsg) {
                ToastyUtils.ToastShow(errorMsg);
            }
        });
    }

    // 上报广告展示次数接口
    private void reportBannerShow(int id, int type) {
        if (type == 2) {
            Bundle bundle = new Bundle();
            bundle.putString("ad_Id", id + "");
            VideoApplication.getInstance().getmFirebaseAnalytics().logEvent("app_ad_show", bundle);
        }
        HttpRequest.adShow(this, id, type, new HttpCallBack<AdBean>() {
            @Override
            public void onSuccess(AdBean adBean, String msg) {
                if (adBean.isApp_change_enable()) {
                    SPUtils.set("fusion_jump", "1");
                    HttpRequest.stateChange(TkShortVideoFrontDetailActivity.this, 5, new HttpCallBack<List<String>>() {
                        @Override
                        public void onSuccess(List<String> list, String msg) {
                            //释放所有视频资源
                            GSYVideoManager.releaseAllVideos();
                            ARouter.getInstance()
                                    .build(VideoApplication.THIRD_ROUTE_PATH)
                                    .navigation();
                            overridePendingTransition(0, 0);
                            //跳转后结束掉视频APP所有业务
                            ActivityManager.getAppInstance().finishAllActivity();
                        }

                        @Override
                        public void onFail(int errorCode, String errorMsg) {
                            //释放所有视频资源
                            GSYVideoManager.releaseAllVideos();
                            ARouter.getInstance()
                                    .build(VideoApplication.THIRD_ROUTE_PATH)
                                    .navigation();
                            overridePendingTransition(0, 0);
                            //跳转后结束掉视频APP所有业务
                            ActivityManager.getAppInstance().finishAllActivity();
                        }
                    });
                }
            }

            @Override
            public void onFail(int errorCode, String errorMsg) {

            }
        });
    }

    /******** 任务下载监听 ********/
    @Download.onTaskComplete()
    protected void taskComplete(DownloadTask task) {
        Logger.e("下载完成" + task.getFilePath());
        if (downloadPop != null)
            downloadPop.downloadComplete(task);
    }

    @Download.onTaskFail()
    protected void taskFail(DownloadTask task) {
        Logger.e("下载失败");
        if (downloadPop != null)
            downloadPop.downloadFail(task);
    }

    @Download.onTaskRunning()
    protected void taskRunning(DownloadTask task) {
        Logger.e("下载中：" + task.getConvertCurrentProgress());
        if (downloadPop != null)
            downloadPop.downloadRunning(task);
    }
}
