package com.shortvideo.lib.ui.activity.concise.ncindex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gyf.immersionbar.ImmersionBar;
import com.shortvideo.lib.R;
import com.shortvideo.lib.common.TkAppConfig;
import com.shortvideo.lib.databinding.TkActivityNcindexPhotosBinding;
import com.shortvideo.lib.ui.activity.concise.photoedit.IMGGalleryActivity;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.MojitoShow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NcPhotosActivity extends AppCompatActivity {

    TkActivityNcindexPhotosBinding binding;

    private NcPhotosAdapter ncPhotosAdapter;

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

        binding = TkActivityNcindexPhotosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
        initListener();
    }

    private void initView() {
        ncPhotosAdapter = new NcPhotosAdapter(new ArrayList<>());
        binding.recycle.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recycle.setAdapter(ncPhotosAdapter);
    }

    private void initData() {
        List<String> path = new ArrayList<>();
        File file = new File(TkAppConfig.MY_PHOTO_PATH);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null)
                for (File f : files) {
                    if (f.isFile() && f.getName().endsWith(".jpg"))
                        path.add(f.getPath());
                }
        }
        ncPhotosAdapter.setList(path);
    }

    private void initListener() {
        binding.back.setOnClickListener(view -> {
            finish();
        });

        binding.sel.setOnClickListener(view -> {
            startActivity(new Intent(this, IMGGalleryActivity.class));
        });

        ncPhotosAdapter.setOnItemClickListener((adapter, view, position) -> {
            MojitoShow.recyclerView(binding.recycle.getContext(), binding.recycle, R.id.img, ncPhotosAdapter.getData(), position);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}
