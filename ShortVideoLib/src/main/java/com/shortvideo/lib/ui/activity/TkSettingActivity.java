package com.shortvideo.lib.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.logger.Logger;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.widgets.TipPop;
import com.shortvideo.lib.databinding.TkActivitySettingBinding;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.DataCleanManager;
import com.shortvideo.lib.utils.ToastyUtils;

public class TkSettingActivity extends AppCompatActivity implements View.OnClickListener {

    TkActivitySettingBinding binding;

    private int clickCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .navigationBarColor(R.color.color_eeeeee)
                .keyboardEnable(true)
                .statusBarDarkFont(true, 0f)
                .init();

        binding = TkActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
        initData();
    }

    private void initView() {
        binding.version.setText(getString(R.string.tk_setting_version, VideoApplication.getInstance().getVerName()));
        binding.name.setText(getString(R.string.tk_setting_package_id, getPackageName()));
        binding.video.setText(getString(R.string.tk_setting_video_id, getIntent().getIntExtra("id", 0)));
        try {
            binding.cache.setText(DataCleanManager.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        binding.img.setOnClickListener(this);
        binding.rlJump.setOnClickListener(this);
        binding.rlClean.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img){
            if (clickCount < 6) {
                clickCount++;
            } else if (clickCount == 8) {
                clickCount = 0;
                binding.llContent.setVisibility(View.GONE);
            } else {
                clickCount++;
                binding.llContent.setVisibility(View.VISIBLE);
            }
        }else if (v.getId() == R.id.rl_jump){
            openGooglePlay();
        }else if (v.getId() == R.id.rl_clean){
            TipPop tipPop = new TipPop(this, () -> {
                DataCleanManager.clearAllCache(TkSettingActivity.this);
                try {
                    binding.cache.setText(DataCleanManager.getTotalCacheSize(TkSettingActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastyUtils.ToastShow(getString(R.string.tk_setting_cache_success));
            });
            tipPop.initTip(getString(R.string.tk_setting_cache_tip_title),  getString(R.string.tk_setting_cache_tip));
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
