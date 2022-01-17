package com.shortvideo.lib.ui.activity.front.mine;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.shortvideo.lib.databinding.TkActivityFrontMyVideoDetailBinding;
import com.shortvideo.lib.utils.ActivityManager;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

public class TkFrontMyVideoDetailActivity extends AppCompatActivity {

    TkActivityFrontMyVideoDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                .statusBarDarkFont(false, 0f)
                .init();

        binding = TkActivityFrontMyVideoDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
        initListener();
    }

    private void initView() {
        //EXOPlayer内核，支持格式更多
        PlayerFactory.setPlayManager(SystemPlayerManager.class);
        //exo缓存模式，支持m3u8，只支持exo
        CacheFactory.setCacheManager(ProxyCacheManager.class);
        //切换渲染模式
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);

        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setIsTouchWiget(false)
                .setRotateViewAuto(false)
                .setRotateWithSystem(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setLooping(true)
                .setNeedLockFull(false)
                .setUrl("file://" + getIntent().getStringExtra("url"))
                .setCacheWithPlay(true)
                .setVideoAllCallBack(new GSYSampleCallBack() {

                    @Override
                    public void onStartPrepared(String url, Object... objects) {
                        super.onStartPrepared(url, objects);
                    }

                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
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

        binding.videoplayer.setLocalVideo(true);
        binding.videoplayer.postDelayed(() -> {
            binding.videoplayer.startPlayLogic();
        }, 500);
    }

    private void initListener() {
        binding.back.setOnClickListener(view -> {
            finish();
        });
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
        //EXOPlayer内核，支持格式更多
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        //exo缓存模式，支持m3u8，只支持exo
        CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
        //切换渲染模式
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        GSYVideoManager.releaseAllVideos();
    }
}
