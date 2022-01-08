package com.fusion.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fusion.app.databinding.ActivityEmptyBinding;
import com.gyf.immersionbar.ImmersionBar;
import com.shortvideo.lib.common.AppConfig;

public class EmptyActivity extends AppCompatActivity {

    ActivityEmptyBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .navigationBarColor(R.color.white)
                .statusBarDarkFont(false, 0f)
                .init();

        binding = ActivityEmptyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        jumpWhere(2);
    }

    /**
     * 自己控制跳转哪里
     * 1.跳转自己的启动页   2.跳转视频的启动页
     */
    private void jumpWhere(int type) {
        if (type == 2) {
            ARouter.getInstance()
                    .build(AppConfig.SHORT_VIDEO_PATH)
                    .navigation();
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        overridePendingTransition(0, 0);
        finish();
    }
}
