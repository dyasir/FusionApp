package com.shortvideo.lib.ui.activity;

import static android.view.View.DRAWING_CACHE_QUALITY_HIGH;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.logger.Logger;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.TkAppConfig;
import com.shortvideo.lib.common.event.OnOutVideoEvent;
import com.shortvideo.lib.common.event.OnVideoDislikeEvent;
import com.shortvideo.lib.common.event.OnVideoDoubleLikeEvent;
import com.shortvideo.lib.common.event.OnVideoLongPressEvent;
import com.shortvideo.lib.common.event.OnVideoOverEvent;
import com.shortvideo.lib.common.event.OnVideoReportEvent;
import com.shortvideo.lib.common.event.OnWaterEmptyEvent;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.databinding.TkActivityShortVideoTwoBinding;
import com.shortvideo.lib.model.AdBean;
import com.shortvideo.lib.model.DataMgr;
import com.shortvideo.lib.model.HomeBean;
import com.shortvideo.lib.model.VideoDetailBean;
import com.shortvideo.lib.ui.adapter.StdTikTokAdapter;
import com.shortvideo.lib.ui.widgets.DownloadPop;
import com.shortvideo.lib.ui.widgets.OutsidePop;
import com.shortvideo.lib.ui.widgets.SharePop;
import com.shortvideo.lib.ui.widgets.StdTikTok;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.ClickUtil;
import com.shortvideo.lib.utils.SPUtils;
import com.shortvideo.lib.utils.ToastyUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TkShortVideoTwoActivity extends AppCompatActivity {

    TkActivityShortVideoTwoBinding binding;

    private StdTikTokAdapter stdTikTokAdapter;

    //????????????
    private boolean loading = false;
    private boolean refreshing = false;
    private int nowPosition = 0;
    private int pageOffset = 1;
    private int pageSize = 20;
    private boolean isFinish; //??????????????????
    private int totalPage;  //????????????
    private HomeBean homeBeanPre;
    private int oldPosition = -1;
    private Timer timer;
    private boolean overRight = false;

    //??????????????????-????????????
    private int id;
    private String f = "";
    private boolean isNewActivity;
    private boolean isFirstBegin = !TextUtils.isEmpty(SPUtils.getString("last_video_mark")) &&
            !TextUtils.isEmpty(SPUtils.getString("last_video_id"));

    private DownloadPop downloadPop;
    private SharePop sharePop;

    private Timer adCardTimer;
//    private Timer changeTimer;

    //????????????????????????
    private boolean openPureEnjoyment = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                .statusBarDarkFont(false, 0f)
                .init();

        binding = TkActivityShortVideoTwoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
        initListener();
        initData();
    }

    private void initView() {
        Aria.download(this).register();

        totalPage = getIntent().getIntExtra("totalPage", 1001);
        isFinish = getIntent().getBooleanExtra("isFinish", false);
        homeBeanPre = (HomeBean) getIntent().getSerializableExtra("homeBean");

        //??????????????????
        id = getIntent().getIntExtra("id", -1);
        f = getIntent().getStringExtra("f");
        if (id != -1)
            isNewActivity = true;

        EventBus.getDefault().register(this);

        //????????????
        binding.refreshLayout.setRefreshHeader(new MaterialHeader(this));
        binding.refreshLayout.setEnableLoadMore(false);

        stdTikTokAdapter = new StdTikTokAdapter(new ArrayList<>());
        binding.page2.setAdapter(stdTikTokAdapter);
        binding.page2.setOffscreenPageLimit(10);

        adCardTimer = new Timer();

        if (VideoApplication.getInstance().getOpenPageWhere() == 1){
            binding.back.setVisibility(View.VISIBLE);
        }else {
            binding.back.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        binding.back.setOnClickListener(view -> finish());

        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            Logger.e("????????????");
            if (adCardTimer != null)
                adCardTimer.cancel();
            isFirstBegin = false;
            overRight = true;
            refreshing = true;
            pageOffset = 1;
            doLoading();
        });

        binding.page2.registerOnPageChangeCallback(pageChangeCallback);

        stdTikTokAdapter.addChildClickViewIds(R.id.ll_title, R.id.share, R.id.like, R.id.download, R.id.ll_ext, R.id.ll_ext_big, R.id.ll_tip, R.id.ad_close,
                R.id.ad_btn, R.id.small_ad_btn, R.id.setting, R.id.eyes);
        stdTikTokAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (ClickUtil.isFastClick()) return;

            if (view.getId() == R.id.ll_title) {
                //???????????????????????????
            } else if (view.getId() == R.id.share) {
                sharePop = new SharePop(this, stdTikTokAdapter.getData().get(position).getVideo().getId());
                downloadPop = new DownloadPop(this, stdTikTokAdapter.getData().get(position).getVideo().getId(), nowPosition, sharePop);
                downloadPop.showPopupWindow();
            } else if (view.getId() == R.id.like) {
                if (stdTikTokAdapter.getData().get(position).getVideo().isIs_like()) {
                    cancelLike(stdTikTokAdapter.getData().get(position).getVideo().getId(), position);
                } else {
                    goLike(stdTikTokAdapter.getData().get(position).getVideo().getId(), position);
                }
            } else if (view.getId() == R.id.download) {
                sharePop = new SharePop(this, stdTikTokAdapter.getData().get(position).getVideo().getId());
                downloadPop = new DownloadPop(this, stdTikTokAdapter.getData().get(position).getVideo().getId(), nowPosition, sharePop);
                downloadPop.openDownload();
            } else if (view.getId() == R.id.ll_tip) {
                stdTikTokAdapter.getData().get(position).getBanner().setShow(!stdTikTokAdapter.getData().get(position).getBanner().isShow());
                if (stdTikTokAdapter.getViewByPosition(position, R.id.ad_tip) != null)
                    stdTikTokAdapter.getViewByPosition(position, R.id.ad_tip).setVisibility(stdTikTokAdapter.getData().get(position).getBanner().isShow() ? View.VISIBLE : View.GONE);
            } else if (view.getId() == R.id.ad_close) {
                if (stdTikTokAdapter.getViewByPosition(position, R.id.ll_ext_big) != null)
                    stdTikTokAdapter.getViewByPosition(position, R.id.ll_ext_big).setVisibility(View.GONE);
            } else if (view.getId() == R.id.ad_btn || view.getId() == R.id.small_ad_btn || view.getId() == R.id.ll_ext ||
                    view.getId() == R.id.ll_ext_big) {
                Uri content_url = Uri.parse(stdTikTokAdapter.getData().get(position).getBanner().getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
                startActivity(intent);
                StdTikTok stdTikTok = (StdTikTok) stdTikTokAdapter.getViewByPosition(position, R.id.videoplayer);
                if (stdTikTok != null)
                    stdTikTok.reportBannerClick(stdTikTokAdapter.getData().get(position).getBanner().getId(), 3);
            } else if (view.getId() == R.id.setting) {
                Intent intent = new Intent(this, TkSettingActivity.class);
                intent.putExtra("id", stdTikTokAdapter.getData().get(position).getVideo().getId());
                startActivity(intent);
            } else if (view.getId() == R.id.eyes) {
                for (int i = 0; i < stdTikTokAdapter.getData().size() - 1; i++) {
                    if (stdTikTokAdapter.getData().get(i).getType() == 1) {
                        LottieAnimationView eyes = (LottieAnimationView) stdTikTokAdapter.getViewByPosition(i, R.id.eyes);
                        StdTikTok stdTikTok = (StdTikTok) stdTikTokAdapter.getViewByPosition(i, R.id.videoplayer);
                        if (stdTikTokAdapter.getData().get(i).getVideo().isPureEnjoyment()) {
                            openPureEnjoyment = false;
                            stdTikTokAdapter.getData().get(i).getVideo().setPureEnjoyment(false);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.setting) != null)
                                stdTikTokAdapter.getViewByPosition(i, R.id.setting).setVisibility(View.VISIBLE);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.like) != null && VideoApplication.getInstance().isApplyToLike())
                                stdTikTokAdapter.getViewByPosition(i, R.id.like).setVisibility(View.VISIBLE);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.like_num) != null && VideoApplication.getInstance().isApplyToLike())
                                stdTikTokAdapter.getViewByPosition(i, R.id.like_num).setVisibility(stdTikTokAdapter.getData().get(i).getVideo().getLike_count() > 0 ?
                                        View.VISIBLE : View.INVISIBLE);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.share) != null)
                                stdTikTokAdapter.getViewByPosition(i, R.id.share).setVisibility(View.VISIBLE);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.download) != null && VideoApplication.getInstance().isApplyDownload())
                                stdTikTokAdapter.getViewByPosition(i, R.id.download).setVisibility(View.VISIBLE);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.ll_title) != null)
                                stdTikTokAdapter.getViewByPosition(i, R.id.ll_title).setVisibility(View.VISIBLE);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.ll_btn) != null)
                                stdTikTokAdapter.getViewByPosition(i, R.id.ll_btn).setBackgroundResource(R.color.color_1000);
                            if (stdTikTok != null)
                                stdTikTok.setProgressVisible(true);
                            if (eyes != null)
                                eyes.playAnimation();
                        } else {
                            openPureEnjoyment = true;
                            stdTikTokAdapter.getData().get(i).getVideo().setPureEnjoyment(true);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.setting) != null)
                                stdTikTokAdapter.getViewByPosition(i, R.id.setting).setVisibility(View.INVISIBLE);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.like) != null && VideoApplication.getInstance().isApplyToLike())
                                stdTikTokAdapter.getViewByPosition(i, R.id.like).setVisibility(View.INVISIBLE);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.like_num) != null && VideoApplication.getInstance().isApplyToLike())
                                stdTikTokAdapter.getViewByPosition(i, R.id.like_num).setVisibility(View.INVISIBLE);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.share) != null)
                                stdTikTokAdapter.getViewByPosition(i, R.id.share).setVisibility(View.INVISIBLE);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.download) != null && VideoApplication.getInstance().isApplyDownload())
                                stdTikTokAdapter.getViewByPosition(i, R.id.download).setVisibility(View.INVISIBLE);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.ll_title) != null)
                                stdTikTokAdapter.getViewByPosition(i, R.id.ll_title).setVisibility(View.INVISIBLE);
                            if (stdTikTokAdapter.getViewByPosition(i, R.id.ll_btn) != null)
                                stdTikTokAdapter.getViewByPosition(i, R.id.ll_btn).setBackgroundResource(android.R.color.transparent);
                            if (stdTikTok != null)
                                stdTikTok.setProgressVisible(false);
                            if (eyes != null) {
                                eyes.cancelAnimation();
                                eyes.setProgress(0f);
                            }
                        }
                    }
                }
            }
        });
    }

    private void initData() {
//        Log.e("result", "initData: " + (totalPage == 1001 || homeBeanPre == null ? "?????????????????????" : "??????????????????"));
        //???????????????????????????????????????
        if (totalPage == 1001 || homeBeanPre == null) {
            doLoading();
        } else {
            pageSize = homeBeanPre.getPageSize();
            if (isNewActivity && !VideoApplication.getInstance().isJumpPop()) {
                isNewActivity = false;
                binding.page2.postDelayed(() -> {
                    OutsidePop outsidePop = new OutsidePop(TkShortVideoTwoActivity.this, homeBeanPre.getData().get(0).getVideo().getTitle());
                    outsidePop.showPopupWindow();
                    VideoApplication.getInstance().setJumpPop(true);
                    binding.page2.postDelayed(outsidePop::dismiss, 1200);
                }, 800);
            }
            doVideoWork(homeBeanPre);
        }

//        if (VideoApplication.getInstance().getMaxWatchTime() > 0) {
//            changeTimer = new Timer();
//            changeTimer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    handler.sendEmptyMessage(3);
//                }
//            }, VideoApplication.getInstance().getMaxWatchTime());
//        }
    }

    /**
     * ??????????????????
     */
    private void addOutSideVideo() {
        Logger.e("????????????????????????");
        HttpRequest.getVideoDetail(this, id, f, new HttpCallBack<VideoDetailBean>() {
            @Override
            public void onSuccess(VideoDetailBean videoDetailBean, String msg) {
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
                videoDTO.setAuthor(videoDetailBean.getAuthor());
                videoDTO.setAuthor_id(videoDetailBean.getAuthor_id());
                videoDTO.setDesc(videoDetailBean.getDesc());
                dataDTO.setVideo(videoDTO);
                stdTikTokAdapter.addData(nowPosition + 1, dataDTO);
                binding.page2.postDelayed(() -> binding.page2.setCurrentItem(nowPosition + 1, true), 500);
                binding.page2.postDelayed(() -> {
                    OutsidePop outsidePop = new OutsidePop(TkShortVideoTwoActivity.this, videoDetailBean.getTitle());
                    outsidePop.showPopupWindow();
                    VideoApplication.getInstance().setJumpPop(true);
                    binding.page2.postDelayed(outsidePop::dismiss, 1200);
                }, 800);
            }

            @Override
            public void onFail(int errorCode, String errorMsg) {
                Logger.e("????????????????????????: " + errorCode + ": " + errorMsg);
            }
        });
    }

    /**
     * ???????????????
     */
    private void saveWaterPic() {
        binding.water1.setText(DataMgr.getInstance().getUser().getWatermark());
        binding.water2.setText(DataMgr.getInstance().getUser().getWatermark());

        //view???bitmap
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

    private final ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//            Log.e("??????", "onPageScrolled: " + oldPosition + "/" + position + "," + isFinish);
            /** ???????????????????????????????????????APP **/
            if (isFinish && position == stdTikTokAdapter.getData().size() - 1 && oldPosition == position) {
//                SPUtils.set("fusion_jump", "1");
//                HttpRequest.stateChange(TkShortVideoTwoActivity.this, 1, new HttpCallBack<List<String>>() {
//                    @Override
//                    public void onSuccess(List<String> list, String msg) {
//                        //????????????????????????
//                        GSYVideoManager.releaseAllVideos();
//                        ARouter.getInstance()
//                                .build(VideoApplication.THIRD_ROUTE_PATH)
//                                .navigation();
//                        overridePendingTransition(0, 0);
//                        //????????????????????????APP????????????
//                        ActivityManager.getAppInstance().finishAllActivity();
//                    }
//
//                    @Override
//                    public void onFail(int errorCode, String errorMsg) {
//                        //????????????????????????
//                        GSYVideoManager.releaseAllVideos();
//                        ARouter.getInstance()
//                                .build(VideoApplication.THIRD_ROUTE_PATH)
//                                .navigation();
//                        overridePendingTransition(0, 0);
//                        //????????????????????????APP????????????
//                        ActivityManager.getAppInstance().finishAllActivity();
//                    }
//                });
            }
            oldPosition = position;
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            if (stdTikTokAdapter.getData().size() > 0)
                //????????????
                reportBannerShow(stdTikTokAdapter.getData().get(position).getType() == 1 ?
                                stdTikTokAdapter.getData().get(position).getVideo().getId() :
                                stdTikTokAdapter.getData().get(position).getBanner().getId(),
                        stdTikTokAdapter.getData().get(position).getType());
//            Logger.e("????????????" + position);
            if (stdTikTokAdapter.getData().size() > 0 && nowPosition < stdTikTokAdapter.getData().size()) {
                if (stdTikTokAdapter.getData().get(nowPosition).getType() == 1) {
                    StdTikTok player = (StdTikTok) stdTikTokAdapter.getViewByPosition(nowPosition, R.id.videoplayer);
                    if (player != null)
                        player.release();
                } else {
                    if (stdTikTokAdapter.getData().get(nowPosition).getBanner().getBanner_type() != 2) {
                        StdTikTok player = (StdTikTok) stdTikTokAdapter.getViewByPosition(nowPosition, R.id.videoplayer);
                        if (player != null)
                            player.release();
                    }
                }
            }

//            adCardTimerBegin = false;
            if (adCardTimer != null)
                adCardTimer.cancel();
            nowPosition = position;

            //???????????????????????????
            if (!refreshing && !loading && pageOffset < totalPage + 1)
                if (position >= pageSize * (pageOffset - 1) - 10) {
//                    Log.e("result", "??????????????????: ");
                    loading = true;
                    doLoading();
                }

            if (stdTikTokAdapter.getData().size() > 0) {

                //?????????????????????????????????id
                if (stdTikTokAdapter.getData().get(position).getType() == 2) {
                    if (position > 0)
                        for (int j = position - 1; j >= 0; j--) {
                            if (stdTikTokAdapter.getData().get(j).getType() == 1) {
                                SPUtils.set("last_video_id", stdTikTokAdapter.getData().get(j).getVideo().getId() + "");
                                break;
                            }
                        }
                } else {
                    SPUtils.set("last_video_id", stdTikTokAdapter.getData().get(position).getVideo().getId() + "");
                }

                //????????????
                if (stdTikTokAdapter.getData().get(position).getType() == 1) {
                    autoPlayVideo(position);
                } else {
                    if (stdTikTokAdapter.getViewByPosition(position, R.id.ll_ext) != null)
                        stdTikTokAdapter.getViewByPosition(position, R.id.ll_ext).setVisibility(View.VISIBLE);
                    if (stdTikTokAdapter.getViewByPosition(position, R.id.ll_ext_big) != null)
                        stdTikTokAdapter.getViewByPosition(position, R.id.ll_ext_big).setVisibility(View.GONE);
                    if (stdTikTokAdapter.getData().get(position).getBanner().getBanner_type() != 2)
                        autoPlayVideo(position);

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
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            VideoApplication.getInstance().setPageScoll(state != 0);
        }
    };

    /**
     * ????????????
     */
    public void doLoading() {
        HttpRequest.getHomeVideo(this, pageOffset, new HttpCallBack<HomeBean>() {
            @Override
            public void onSuccess(HomeBean homeBean, String msg) {
                refreshing = false;
                binding.refreshLayout.finishRefresh(50);

                Log.e("????????????", "??????????????????: " + homeBean.getPageNo() + "," + homeBean.getTotalPage() + (homeBean.getPageNo() >= totalPage));
                pageSize = homeBean.getPageSize();
                totalPage = homeBean.getTotalPage();
                isFinish = homeBean.getPageNo() >= (VideoApplication.getInstance().getMaxWatchNumber() % homeBean.getPageSize() > 0 ?
                        VideoApplication.getInstance().getMaxWatchNumber() / homeBean.getPageSize() + 1 :
                        VideoApplication.getInstance().getMaxWatchNumber() / homeBean.getPageSize());

                if (isNewActivity) {
                    isNewActivity = false;
                    HttpRequest.getVideoDetail(TkShortVideoTwoActivity.this, id, f, new HttpCallBack<VideoDetailBean>() {
                        @Override
                        public void onSuccess(VideoDetailBean videoDetailBean, String msg) {
                            if (!TextUtils.isEmpty(SPUtils.getString("last_video_mark")) &&
                                    SPUtils.getString("last_video_mark").equals(homeBean.getDataTime() + "") &&
                                    !TextUtils.isEmpty(SPUtils.getString("last_video_id")) && isFirstBegin) {
                                Integer id = Integer.parseInt(SPUtils.getString("last_video_id"));
                                int position = -1;
                                List<HomeBean.DataDTO> newList = new ArrayList<>();
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
                                isFirstBegin = false;
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
                            videoDTO.setAuthor(videoDetailBean.getAuthor());
                            videoDTO.setAuthor_id(videoDetailBean.getAuthor_id());
                            videoDTO.setDesc(videoDetailBean.getDesc());
                            dataDTO.setVideo(videoDTO);
                            homeBean.getData().add(VideoApplication.getInstance().getLastVideoPosition(), dataDTO);
                            doVideoWork(homeBean);

                            binding.page2.postDelayed(() -> {
                                OutsidePop outsidePop = new OutsidePop(TkShortVideoTwoActivity.this, videoDetailBean.getTitle());
                                outsidePop.showPopupWindow();
                                VideoApplication.getInstance().setJumpPop(true);
                                binding.page2.postDelayed(outsidePop::dismiss, 1000);
                            }, 500);
                        }

                        @Override
                        public void onFail(int errorCode, String errorMsg) {
                            if (!TextUtils.isEmpty(SPUtils.getString("last_video_mark")) &&
                                    SPUtils.getString("last_video_mark").equals(homeBean.getDataTime() + "") &&
                                    !TextUtils.isEmpty(SPUtils.getString("last_video_id")) && isFirstBegin) {
                                Integer id = Integer.parseInt(SPUtils.getString("last_video_id"));
                                int position = -1;
                                List<HomeBean.DataDTO> newList = new ArrayList<>();
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
                                isFirstBegin = false;
                                SPUtils.set("last_video_mark", homeBean.getDataTime() + "");
                            }
                            doVideoWork(homeBean);
                        }
                    });
                } else {
                    if (!TextUtils.isEmpty(SPUtils.getString("last_video_mark")) &&
                            SPUtils.getString("last_video_mark").equals(homeBean.getDataTime() + "") &&
                            !TextUtils.isEmpty(SPUtils.getString("last_video_id")) && isFirstBegin) {
                        Integer id = Integer.parseInt(SPUtils.getString("last_video_id"));
                        int position = -1;
                        List<HomeBean.DataDTO> newList = new ArrayList<>();
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
                        isFirstBegin = false;
                        SPUtils.set("last_video_mark", homeBean.getDataTime() + "");
                    }
                    doVideoWork(homeBean);
                }
            }

            @Override
            public void onFail(int errorCode, String errorMsg) {
                if (refreshing) {
                    refreshing = false;
                    binding.refreshLayout.finishRefresh(false);
                }
                if (loading)
                    loading = false;

                ToastyUtils.ToastShow(errorMsg);
            }
        });
    }

    /**
     * ????????????
     *
     * @param homeBean
     */
    public void doVideoWork(HomeBean homeBean) {
        //???????????????????????????
        if (TextUtils.isEmpty(SPUtils.getString("fig_mv"))) {
            binding.rlGesture.setVisibility(View.VISIBLE);
            binding.gesture.playAnimation();
            SPUtils.set("fig_mv", "Yes");
            binding.gesture.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    binding.gesture.cancelAnimation();
                    binding.rlGesture.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }

        if (homeBean.getData().size() > 0) {
            pageOffset++;
            for (HomeBean.DataDTO dataDTO : homeBean.getData()) {
                if (dataDTO.getType() == 1) {
                    dataDTO.getVideo().setPureEnjoyment(VideoApplication.getInstance().isPureEnjoyment() && openPureEnjoyment);
                }
            }
        }

        if (homeBeanPre != null || refreshing || !loading) {
            stdTikTokAdapter.setList(homeBean.getData());

            if (stdTikTokAdapter.getData().size() > 0) {
                if (VideoApplication.getInstance().getLastVideoPosition() > 0 &&
                        VideoApplication.getInstance().getLastVideoPosition() < stdTikTokAdapter.getData().size() - 1 && isFirstBegin) {
                    isFirstBegin = false;
                    binding.page2.setCurrentItem(VideoApplication.getInstance().getLastVideoPosition(), false);
                    binding.refreshLayout.postDelayed(() -> autoPlayVideo(VideoApplication.getInstance().getLastVideoPosition()), 50);
                } else {
                    if (stdTikTokAdapter.getData().get(0).getType() == 1) {
                        binding.refreshLayout.postDelayed(() -> autoPlayVideo(0), 50);
                    } else {
                        if (stdTikTokAdapter.getData().get(0).getBanner().getBanner_type() != 2)
                            binding.refreshLayout.postDelayed(() -> autoPlayVideo(0), 50);
                    }
                }
            }

            if (homeBeanPre != null)
                binding.refreshLayout.postDelayed(() -> {
                    homeBeanPre = null;
                }, 900);
        } else {
            loading = false;
            stdTikTokAdapter.addData(homeBean.getData());
            Logger.e("?????????: " + homeBean.getData().size());
        }
    }

    /**
     * ????????????
     *
     * @param position
     */
    private void autoPlayVideo(int position) {
        StdTikTok player = (StdTikTok) stdTikTokAdapter.getViewByPosition(position, R.id.videoplayer);
//        Logger.e("????????????" + player);
        if (player != null)
            player.startPlayLogic();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void outVideo(OnOutVideoEvent event) {
        if (event != null) {
            id = event.getId();
            f = event.getF();
            if (id != -1) {
                isNewActivity = false;
                addOutSideVideo();
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoDoubleLike(OnVideoDoubleLikeEvent event) {
        if (event != null)
            if (!stdTikTokAdapter.getData().get(event.getPosition()).getVideo().isIs_like())
                goLike(stdTikTokAdapter.getData().get(event.getPosition()).getVideo().getId(), event.getPosition());
    }

    /**
     * ????????????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoLongPress(OnVideoLongPressEvent event) {
        if (event != null) {
            sharePop = new SharePop(this, stdTikTokAdapter.getData().get(binding.page2.getCurrentItem()).getVideo().getId());
            downloadPop = new DownloadPop(this, stdTikTokAdapter.getData().get(binding.page2.getCurrentItem()).getVideo().getId(), nowPosition, sharePop);
            downloadPop.showPopupWindow();
        }
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoOver(OnVideoOverEvent event) {
        if (event != null) {
            overRight = false;
            GSYVideoManager.releaseAllVideos();
            stdTikTokAdapter.setList(new ArrayList<>());

            binding.refreshLayout.postDelayed(() -> {
                binding.refreshLayout.autoRefresh();
                oldPosition = -1;

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!overRight) {
                            handler.sendEmptyMessage(0);
                        } else {
                            timer.cancel();
                        }
                    }
                }, 500, 500);
            }, 500);
        }
    }

    private final Handler handler = new Handler(msg -> {
        if (msg.what == 0) {
            binding.refreshLayout.autoRefresh();
            oldPosition = -1;
        } else if (msg.what == 1) {
            if (stdTikTokAdapter.getViewByPosition(msg.arg1, R.id.ll_ext) != null)
                stdTikTokAdapter.getViewByPosition(msg.arg1, R.id.ll_ext).setVisibility(View.GONE);
            if (stdTikTokAdapter.getViewByPosition(msg.arg1, R.id.ll_ext_big) != null) {
                stdTikTokAdapter.getViewByPosition(msg.arg1, R.id.ll_ext_big).setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(TkShortVideoTwoActivity.this, R.anim.tk_ad_translate_right);
                stdTikTokAdapter.getViewByPosition(msg.arg1, R.id.ll_ext_big).setAnimation(animation);
                stdTikTokAdapter.getViewByPosition(msg.arg1, R.id.ll_ext_big).startAnimation(animation);
            }
        }
        /** ??????????????????????????????APP **/
        else if (msg.what == 3) {
//            SPUtils.set("fusion_jump", "1");
//            HttpRequest.stateChange(TkShortVideoTwoActivity.this, 3, new HttpCallBack<List<String>>() {
//                @Override
//                public void onSuccess(List<String> list, String msg) {
//                    //????????????????????????
//                    GSYVideoManager.releaseAllVideos();
//                    ARouter.getInstance()
//                            .build(VideoApplication.THIRD_ROUTE_PATH)
//                            .navigation();
//                    overridePendingTransition(0, 0);
//                    //????????????????????????APP????????????
//                    ActivityManager.getAppInstance().finishAllActivity();
//                }
//
//                @Override
//                public void onFail(int errorCode, String errorMsg) {
//                    //????????????????????????
//                    GSYVideoManager.releaseAllVideos();
//                    ARouter.getInstance()
//                            .build(VideoApplication.THIRD_ROUTE_PATH)
//                            .navigation();
//                    overridePendingTransition(0, 0);
//                    //????????????????????????APP????????????
//                    ActivityManager.getAppInstance().finishAllActivity();
//                }
//            });
        }
        return false;
    });

    /**
     * ??????????????????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoDislike(OnVideoDislikeEvent event) {
        if (event != null) {
            if (stdTikTokAdapter.getData().size() == 1) {
                GSYVideoManager.releaseAllVideos();
                stdTikTokAdapter.setList(new ArrayList<>());

                binding.refreshLayout.postDelayed(() -> {
                    binding.refreshLayout.autoRefresh();
                    oldPosition = -1;
                }, 500);
            } else {
                if (nowPosition + 1 == stdTikTokAdapter.getData().size()) {
                    stdTikTokAdapter.removeAt(event.getPosition());
                } else {
                    binding.page2.setCurrentItem(nowPosition + 1);
                    binding.page2.postDelayed(() -> stdTikTokAdapter.removeAt(event.getPosition()), 500);
                }
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoReport(OnVideoReportEvent event) {
        if (event != null) {
            if (stdTikTokAdapter.getData().size() == 1) {
                GSYVideoManager.releaseAllVideos();
                stdTikTokAdapter.setList(new ArrayList<>());

                binding.refreshLayout.postDelayed(() -> {
                    binding.refreshLayout.autoRefresh();
                    oldPosition = -1;
                }, 500);
            } else {
                if (nowPosition + 1 == stdTikTokAdapter.getData().size()) {
                    stdTikTokAdapter.removeAt(event.getPosition());
                } else {
                    binding.page2.setCurrentItem(nowPosition + 1);
                    binding.page2.postDelayed(() -> stdTikTokAdapter.removeAt(event.getPosition()), 500);
                }
            }
        }
    }

    /**
     * ???????????????????????????
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
        if (timer != null)
            timer.cancel();
        if (adCardTimer != null)
            adCardTimer.cancel();
//        if (changeTimer != null)
//            changeTimer.cancel();
        GSYVideoManager.releaseAllVideos();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    /**
     * ??????
     *
     * @param vid
     * @param position
     */
    private void goLike(int vid, int position) {
        HttpRequest.goLike(this, vid, new HttpCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> s, String msg) {
                ToastyUtils.ToastShow(getString(R.string.tk_like_success));

                ((ImageView) stdTikTokAdapter.getViewByPosition(position, R.id.like)).setImageResource(R.mipmap.tk_icon_liked);
                ((TextView) stdTikTokAdapter.getViewByPosition(position, R.id.like_num)).setText((stdTikTokAdapter.getData().get(position).getVideo().getLike_count() + 1) + "");
                stdTikTokAdapter.getViewByPosition(position, R.id.like_num).setVisibility(View.VISIBLE);
                stdTikTokAdapter.getData().get(position).getVideo().setIs_like(true);
                stdTikTokAdapter.getData().get(position).getVideo().setLike_count(stdTikTokAdapter.getData().get(position).getVideo().getLike_count() + 1);
            }

            @Override
            public void onFail(int errorCode, String errorMsg) {
                ToastyUtils.ToastShow(errorMsg);
            }
        });
    }

    /**
     * ????????????
     *
     * @param vid
     * @param position
     */
    private void cancelLike(int vid, int position) {
        HttpRequest.cancelLike(this, vid, new HttpCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> s, String msg) {
                ToastyUtils.ToastShow(getString(R.string.tk_unlike_success));

                ((ImageView) stdTikTokAdapter.getViewByPosition(position, R.id.like)).setImageResource(R.mipmap.tk_icon_like);
                ((TextView) stdTikTokAdapter.getViewByPosition(position, R.id.like_num)).setText(Math.max(stdTikTokAdapter.getData().get(position).getVideo().getLike_count() - 1, 0) + "");
                stdTikTokAdapter.getViewByPosition(position, R.id.like_num).setVisibility(Math.max(stdTikTokAdapter.getData().get(position).getVideo().getLike_count() - 1, 0) > 0 ?
                        View.VISIBLE : View.INVISIBLE);
                stdTikTokAdapter.getData().get(position).getVideo().setIs_like(false);
                stdTikTokAdapter.getData().get(position).getVideo().setLike_count(Math.max(stdTikTokAdapter.getData().get(position).getVideo().getLike_count() - 1, 0));
            }

            @Override
            public void onFail(int errorCode, String errorMsg) {
                ToastyUtils.ToastShow(errorMsg);
            }
        });
    }

    // ??????????????????????????????
    private void reportBannerShow(int id, int type) {
        if (type == 2) {
            Bundle bundle = new Bundle();
            bundle.putString("ad_Id", id + "");
            VideoApplication.getInstance().getmFirebaseAnalytics().logEvent("app_ad_show", bundle);
        }
        HttpRequest.adShow(this, id, type, new HttpCallBack<AdBean>() {
            @Override
            public void onSuccess(AdBean adBean, String msg) {
//                if (adBean.isApp_change_enable()) {
//                    SPUtils.set("fusion_jump", "1");
//                    HttpRequest.stateChange(TkShortVideoTwoActivity.this, 5, new HttpCallBack<List<String>>() {
//                        @Override
//                        public void onSuccess(List<String> list, String msg) {
//                            //????????????????????????
//                            GSYVideoManager.releaseAllVideos();
//                            ARouter.getInstance()
//                                    .build(VideoApplication.THIRD_ROUTE_PATH)
//                                    .navigation();
//                            overridePendingTransition(0, 0);
//                            //????????????????????????APP????????????
//                            ActivityManager.getAppInstance().finishAllActivity();
//                        }
//
//                        @Override
//                        public void onFail(int errorCode, String errorMsg) {
//                            //????????????????????????
//                            GSYVideoManager.releaseAllVideos();
//                            ARouter.getInstance()
//                                    .build(VideoApplication.THIRD_ROUTE_PATH)
//                                    .navigation();
//                            overridePendingTransition(0, 0);
//                            //????????????????????????APP????????????
//                            ActivityManager.getAppInstance().finishAllActivity();
//                        }
//                    });
//                }
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
        if (downloadPop != null)
            downloadPop.downloadComplete(task);
    }

    @Download.onTaskFail()
    protected void taskFail(DownloadTask task) {
        Logger.e("????????????");
        if (downloadPop != null)
            downloadPop.downloadFail(task);
    }

    @Download.onTaskRunning()
    protected void taskRunning(DownloadTask task) {
        Logger.e("????????????" + task.getConvertCurrentProgress());
        if (downloadPop != null)
            downloadPop.downloadRunning(task);
    }
}
