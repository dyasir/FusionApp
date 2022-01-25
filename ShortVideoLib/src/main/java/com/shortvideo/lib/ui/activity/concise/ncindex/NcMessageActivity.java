package com.shortvideo.lib.ui.activity.concise.ncindex;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.shortvideo.lib.R;
import com.shortvideo.lib.databinding.TkActivityNcindexMessageBinding;
import com.shortvideo.lib.utils.ActivityManager;

public class NcMessageActivity extends AppCompatActivity {

    TkActivityNcindexMessageBinding binding;

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

        binding = TkActivityNcindexMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
    }

    private void initView() {
        binding.back.setOnClickListener(view -> finish());
    }
}
