package com.shortvideo.lib.ui.activity.concise.ncindex;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.TkAppConfig;
import com.shortvideo.lib.common.widgets.TipPop;
import com.shortvideo.lib.databinding.TkActivityNcindexMineBinding;
import com.shortvideo.lib.ui.activity.front.mine.TkFrontMyVideoActivity;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.DataCleanManager;
import com.shortvideo.lib.utils.TimeDateUtils;
import com.shortvideo.lib.utils.ToastyUtils;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.IOException;
import java.util.List;

public class NcMineActivity extends AppCompatActivity implements View.OnClickListener {

    TkActivityNcindexMineBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .navigationBarColor(R.color.white)
                .keyboardEnable(true)
                .statusBarDarkFont(true, 0f)
                .init();

        binding = TkActivityNcindexMineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
        initData();
    }

    private void initView() {
        /** 自定义属性部分开始 **/
        binding.video.setVisibility(VideoApplication.getInstance().isApplyFrontPageTakeVideo() ? View.VISIBLE : View.GONE);
        binding.rlVideo.setVisibility(VideoApplication.getInstance().isApplyFrontHomeVideo() &&
                VideoApplication.getInstance().isApplyFrontPageTakeVideo() ? View.VISIBLE : View.GONE);
        /** 自定义属性部分结束 **/

        binding.back.setOnClickListener(view -> finish());
    }

    private void initData() {
        binding.version.setText(getString(R.string.tk_setting_version, VideoApplication.getInstance().getVerName()));
        try {
            binding.cache.setText(DataCleanManager.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.rlJump.setOnClickListener(this);
        binding.rlClean.setOnClickListener(this);
        binding.rlVideo.setOnClickListener(this);
        binding.video.setOnClickListener(this);
    }

    @SuppressLint("AutoDispose")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rl_jump) {
            openGooglePlay();
        } else if (view.getId() == R.id.rl_clean) {
            TipPop tipPop = new TipPop(this, () -> {
                DataCleanManager.clearAllCache(NcMineActivity.this);
                try {
                    binding.cache.setText(DataCleanManager.getTotalCacheSize(NcMineActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastyUtils.ToastShow(getString(R.string.tk_setting_cache_success));
            });
            tipPop.initTip(getString(R.string.tk_setting_cache_tip_title), getString(R.string.tk_setting_cache_tip));
        } else if (view.getId() == R.id.rl_video) {
            startActivity(new Intent(this, TkFrontMyVideoActivity.class));
        } else if (view.getId() == R.id.video) {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            PictureSelector.create(this)
                                    .openCamera(PictureMimeType.ofVideo())
                                    .forResult(new OnResultCallbackListener<LocalMedia>() {
                                        @Override
                                        public void onResult(List<LocalMedia> result) {
                                            if (result != null && result.size() > 0) {
                                                try {
                                                    PictureFileUtils.copyFile(PictureFileUtils.getPath(binding.video.getContext(), Uri.parse(result.get(0).getPath())),
                                                            TkAppConfig.MY_VIDEO_PATH + TimeDateUtils.getCurTimeLong() + ".mp4");
                                                    startActivity(new Intent(NcMineActivity.this, TkFrontMyVideoActivity.class));
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancel() {

                                        }
                                    });
                        }
                    });
        }
    }

    /**
     * 跳转谷歌市场对应应用
     */
    private void openGooglePlay() {
        try {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.android.vending");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
