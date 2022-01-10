package com.shortvideo.lib.ui.activity;

import static android.view.View.DRAWING_CACHE_QUALITY_HIGH;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.launcher.ARouter;
import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.logger.Logger;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.AppConfig;
import com.shortvideo.lib.common.event.OnOutVideoEvent;
import com.shortvideo.lib.common.event.OnVideoDislikeEvent;
import com.shortvideo.lib.common.event.OnVideoDoubleLikeEvent;
import com.shortvideo.lib.common.event.OnVideoLongPressEvent;
import com.shortvideo.lib.common.event.OnVideoOverEvent;
import com.shortvideo.lib.common.event.OnVideoReportEvent;
import com.shortvideo.lib.common.event.OnWaterEmptyEvent;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.databinding.TkActivityShortVideoBinding;
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

import net.mikaelzero.mojito.view.sketch.core.Sketch;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ShortVideoActivity extends AppCompatActivity {

    TkActivityShortVideoBinding binding;

    private StdTikTokAdapter stdTikTokAdapter;

    //刷新相关
    private boolean loading = false;
    private boolean refreshing = false;
    private int nowPosition = 0;
    private int pageOffset = 1;
    private int pageSize = 20;
    private boolean isFinish; //视频加载完了
    private int totalPage;  //总页码数
    private HomeBean homeBeanPre;
    private int oldPosition = -1;
    private Timer timer;
    private boolean overRight = false;

    //外部拉起应用-添加视频
    private int id;
    private String f = "";
    private boolean isNewActivity;
    private boolean isFirstBegin = !TextUtils.isEmpty(SPUtils.getString("last_video_mark")) &&
            !TextUtils.isEmpty(SPUtils.getString("last_video_id"));

    private DownloadPop downloadPop;
    private SharePop sharePop;

    private Timer adCardTimer;
    private Timer changeTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                .statusBarDarkFont(false, 0f)
                .init();

        binding = TkActivityShortVideoBinding.inflate(getLayoutInflater());
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

        //外部调起相关
        id = getIntent().getIntExtra("id", -1);
        f = getIntent().getStringExtra("f");
        if (id != -1)
            isNewActivity = true;

        EventBus.getDefault().register(this);

        //设置刷新
        binding.refreshLayout.setRefreshHeader(new MaterialHeader(this));
        binding.refreshLayout.setEnableLoadMore(false);

        stdTikTokAdapter = new StdTikTokAdapter(new ArrayList<>());
        binding.page2.setAdapter(stdTikTokAdapter);
        binding.page2.setOffscreenPageLimit(10);

        adCardTimer = new Timer();
    }

    private void initListener() {
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            Logger.e("开始刷新");
            isFirstBegin = false;
            overRight = true;
            refreshing = true;
            pageOffset = 1;
            doLoading();
        });

        binding.page2.registerOnPageChangeCallback(pageChangeCallback);

        stdTikTokAdapter.addChildClickViewIds(R.id.ll_title, R.id.share, R.id.like, R.id.download, R.id.ll_ext, R.id.ll_ext_big, R.id.ll_tip, R.id.ad_close,
                R.id.ad_btn, R.id.small_ad_btn, R.id.setting);
        stdTikTokAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (ClickUtil.isFastClick()) return;

            if (view.getId() == R.id.ll_title) {
                //无效点击，屏蔽点赞
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
                Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }

    private void initData() {
//        Log.e("result", "initData: " + (totalPage == 1001 || homeBeanPre == null ? "预加载未成功！" : "预加载成功！"));
        //预加载未成功，开始加载数据
        if (totalPage == 1001 || homeBeanPre == null) {
            doLoading();
        } else {
            pageSize = homeBeanPre.getPageSize();
            if (isNewActivity && !VideoApplication.getInstance().isJumpPop()) {
                isNewActivity = false;
                binding.page2.postDelayed(() -> {
                    OutsidePop outsidePop = new OutsidePop(ShortVideoActivity.this, homeBeanPre.getData().get(0).getVideo().getTitle());
                    outsidePop.showPopupWindow();
                    VideoApplication.getInstance().setJumpPop(true);
                    binding.page2.postDelayed(outsidePop::dismiss, 1200);
                }, 800);
            }
            doVideoWork(homeBeanPre);
        }

        if (VideoApplication.getInstance().getMaxWatchTime() > 0) {
            changeTimer = new Timer();
            changeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(3);
                }
            }, VideoApplication.getInstance().getMaxWatchTime());
        }
    }

    /**
     * 添加外部视频
     */
    private void addOutSideVideo() {
        Logger.e("开始添加外部视频");
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
                    OutsidePop outsidePop = new OutsidePop(ShortVideoActivity.this, videoDetailBean.getTitle());
                    outsidePop.showPopupWindow();
                    VideoApplication.getInstance().setJumpPop(true);
                    binding.page2.postDelayed(outsidePop::dismiss, 1200);
                }, 800);
            }

            @Override
            public void onFail(int errorCode, String errorMsg) {
                Logger.e("添加外部视频失败: " + errorCode + ": " + errorMsg);
            }
        });
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

            VideoApplication.getInstance().setWaterPicPath(AppConfig.getWaterPath(bitmap));
        }, 500);
    }

    private final ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//            Log.e("弹窗", "onPageScrolled: " + oldPosition + "/" + position + "," + isFinish);
            /** 满足刷新上限，直接跳转其他APP **/
            if (isFinish && position == stdTikTokAdapter.getData().size() - 1 && oldPosition == position) {
                HttpRequest.stateChange(ShortVideoActivity.this, 1, new HttpCallBack<List<String>>() {
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
            oldPosition = position;
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            if (stdTikTokAdapter.getData().size() > 0)
                //上报广告
                reportBannerShow(stdTikTokAdapter.getData().get(position).getType() == 1 ?
                                stdTikTokAdapter.getData().get(position).getVideo().getId() :
                                stdTikTokAdapter.getData().get(position).getBanner().getId(),
                        stdTikTokAdapter.getData().get(position).getType());
//            Logger.e("第几页：" + position);
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
            adCardTimer.cancel();
            nowPosition = position;

            //判断是否开启预加载
            if (!refreshing && !loading && pageOffset < totalPage + 1)
                if (position >= pageSize * (pageOffset - 1) - 10) {
//                    Log.e("result", "开始加载更多: ");
                    loading = true;
                    doLoading();
                }

            if (stdTikTokAdapter.getData().size() > 0) {

                //记录最后一次播放的视频id
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

                //自动播放
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
     * 加载数据
     */
    public void doLoading() {
        HttpRequest.getHomeVideo(this, pageOffset, new HttpCallBack<HomeBean>() {
            @Override
            public void onSuccess(HomeBean homeBean, String msg) {
                refreshing = false;
                binding.refreshLayout.finishRefresh(50);

                Log.e("视频接口", "视频接口结果: " + homeBean.getPageNo() + "," + homeBean.getTotalPage() + (homeBean.getPageNo() >= totalPage));
                pageSize = homeBean.getPageSize();
                totalPage = homeBean.getTotalPage();
                isFinish = homeBean.getPageNo() >= (VideoApplication.getInstance().getMaxWatchNumber() % homeBean.getPageSize() > 0 ?
                        VideoApplication.getInstance().getMaxWatchNumber() / homeBean.getPageSize() + 1 :
                        VideoApplication.getInstance().getMaxWatchNumber() / homeBean.getPageSize());

                if (isNewActivity) {
                    isNewActivity = false;
                    HttpRequest.getVideoDetail(ShortVideoActivity.this, id, f, new HttpCallBack<VideoDetailBean>() {
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
                                OutsidePop outsidePop = new OutsidePop(ShortVideoActivity.this, videoDetailBean.getTitle());
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
     * 数据处理
     *
     * @param homeBean
     */
    public void doVideoWork(HomeBean homeBean) {
        //是否显示手势引导图
        if (TextUtils.isEmpty(SPUtils.getString("fig_mv"))) {
            binding.imgMv.getOptions().setDecodeGifImage(true);
            Sketch.with(this).displayFromResource(R.drawable.fig_mv, binding.imgMv)
                    .decodeGifImage()
                    .commit();
            SPUtils.set("fig_mv", "Yes");

            binding.imgMv.postDelayed(() -> binding.imgMv.setVisibility(View.GONE), 2000L);
        }

        if (homeBean.getData().size() > 0)
            pageOffset++;

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
            Logger.e("数据量: " + homeBean.getData().size());
        }
    }

    /**
     * 自动播放
     *
     * @param position
     */
    private void autoPlayVideo(int position) {
        StdTikTok player = (StdTikTok) stdTikTokAdapter.getViewByPosition(position, R.id.videoplayer);
//        Logger.e("自动播放" + player);
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
     * 视频双击事件
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
     * 视频长按
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
     * 可看视频播放完毕后，重新新一轮视频播放
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

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 0) {
                binding.refreshLayout.autoRefresh();
                oldPosition = -1;
            } else if (msg.what == 1) {
                if (stdTikTokAdapter.getViewByPosition(msg.arg1, R.id.ll_ext) != null)
                    stdTikTokAdapter.getViewByPosition(msg.arg1, R.id.ll_ext).setVisibility(View.GONE);
                if (stdTikTokAdapter.getViewByPosition(msg.arg1, R.id.ll_ext_big) != null) {
                    stdTikTokAdapter.getViewByPosition(msg.arg1, R.id.ll_ext_big).setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(ShortVideoActivity.this, R.anim.ad_translate);
                    stdTikTokAdapter.getViewByPosition(msg.arg1, R.id.ll_ext_big).setAnimation(animation);
                    stdTikTokAdapter.getViewByPosition(msg.arg1, R.id.ll_ext_big).startAnimation(animation);
                }
            }
            /** 到达总时长，跳转其他APP **/
            else if (msg.what == 3) {
                HttpRequest.stateChange(ShortVideoActivity.this, 3, new HttpCallBack<List<String>>() {
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
            return false;
        }
    });

    /**
     * 不喜欢该视频
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
     * 举报视频成功
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
        if (changeTimer != null)
            changeTimer.cancel();
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
                ToastyUtils.ToastShow("Như thành công");

                ((ImageView) stdTikTokAdapter.getViewByPosition(position, R.id.like)).setImageResource(R.mipmap.icon_liked);
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
     * 取消点赞
     *
     * @param vid
     * @param position
     */
    private void cancelLike(int vid, int position) {
        HttpRequest.cancelLike(this, vid, new HttpCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> s, String msg) {
                ToastyUtils.ToastShow("Hủy like thành công");

                ((ImageView) stdTikTokAdapter.getViewByPosition(position, R.id.like)).setImageResource(R.mipmap.icon_like);
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
