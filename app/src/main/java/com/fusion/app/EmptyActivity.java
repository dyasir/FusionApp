package com.fusion.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusion.app.databinding.ActivityEmptyBinding;
import com.gyf.immersionbar.ImmersionBar;

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
    }
}
