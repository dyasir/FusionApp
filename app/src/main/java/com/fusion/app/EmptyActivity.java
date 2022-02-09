package com.fusion.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.fusion.app.databinding.ActivityEmptyBinding;
import com.fusion.switchlib.SwitchBaseActivity;
import com.fusion.switchlib.SwitchJumpListener;
import com.gyf.immersionbar.ImmersionBar;

public class EmptyActivity extends SwitchBaseActivity implements SwitchJumpListener {

    ActivityEmptyBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSwitchJumpListener(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .navigationBarColor(R.color.white)
                .statusBarDarkFont(false, 0f)
                .init();

        binding = ActivityEmptyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void jumpPackageA() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    public void jumpPackageB() {
        startActivity(new Intent(this, FusionBActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }
}
