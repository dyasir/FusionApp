package com.shortvideo.lib.ui.activity.concise.ncindex;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.gyf.immersionbar.ImmersionBar;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.databinding.TkActivityNcindexWallpaperBinding;
import com.shortvideo.lib.model.WallpaperBean;
import com.shortvideo.lib.ui.activity.front.photos.TkFrontPhotosAdapter;
import com.shortvideo.lib.ui.activity.front.photos.TkFrontPhotosDetailActivity;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.MojitoShow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NcWallpaperActivity extends AppCompatActivity {

    TkActivityNcindexWallpaperBinding binding;

    private TkFrontPhotosAdapter tkFrontPhotosAdapter;
    private final List<String> photoList = new ArrayList<>();
    private boolean isRefresh = false;
    private ActivityResultLauncher launcher;

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

        binding = TkActivityNcindexWallpaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
        initData();
    }

    private void initView() {
        tkFrontPhotosAdapter = new TkFrontPhotosAdapter(new ArrayList<>());
        binding.recycle.setLayoutManager(new GridLayoutManager(binding.recycle.getContext(), VideoApplication.getInstance().getFrontPhotosSpanCount()));
        binding.recycle.setAdapter(tkFrontPhotosAdapter);

        binding.refresh.setRefreshHeader(new MaterialHeader(binding.refresh.getContext()));
        binding.refresh.setEnableLoadMore(false);
        binding.refresh.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            initData();
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null)
                binding.recycle.scrollToPosition(result.getData().getIntExtra("nowPosition", 0));
        });

        tkFrontPhotosAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (VideoApplication.getInstance().isApplyFrontPhotosWallpaper()) {
                Intent intent = new Intent(NcWallpaperActivity.this, TkFrontPhotosDetailActivity.class);
                intent.putExtra("list", (Serializable) photoList);
                intent.putExtra("position", position);
                launcher.launch(intent);
            } else {
                MojitoShow.recyclerView(binding.recycle.getContext(), binding.recycle, R.id.img, photoList, position);
            }
        });

        binding.back.setOnClickListener(view -> finish());
    }

    private void initData() {
        HttpRequest.getHotWallpaper(this, 200, new HttpCallBack<WallpaperBean>() {
            @Override
            public void onSuccess(WallpaperBean wallpaperBean, String msg) {
                if (isRefresh) {
                    isRefresh = false;
                    binding.refresh.finishRefresh(50);
                }
                photoList.clear();
                for (WallpaperBean.ImagesDTO imagesDTO : wallpaperBean.getImages()) {
                    if (TextUtils.isEmpty(imagesDTO.getOrigin())) {
                        if (TextUtils.isEmpty(imagesDTO.getImg())) {
                            photoList.add(imagesDTO.getUplay());
                        } else {
                            photoList.add(imagesDTO.getImg());
                        }
                    } else {
                        photoList.add(imagesDTO.getOrigin());
                    }
                }
                tkFrontPhotosAdapter.setList(wallpaperBean.getImages());
            }

            @Override
            public void onFail(int errorCode, String errorMsg) {
                if (isRefresh) {
                    isRefresh = false;
                    binding.refresh.finishRefresh(50);
                }
            }
        });
    }
}
