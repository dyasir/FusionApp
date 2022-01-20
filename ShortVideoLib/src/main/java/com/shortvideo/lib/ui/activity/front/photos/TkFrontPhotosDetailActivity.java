package com.shortvideo.lib.ui.activity.front.photos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.shortvideo.lib.R;
import com.shortvideo.lib.databinding.TkActivityFrontPhotosDetailBinding;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.ClickUtil;
import com.shortvideo.lib.utils.ToastyUtils;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TkFrontPhotosDetailActivity extends AppCompatActivity {

    TkActivityFrontPhotosDetailBinding binding;

    private List<String> imgList = new ArrayList<>();
    private int nowPosition;
    private boolean isHidden = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                .init();

        binding = TkActivityFrontPhotosDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
    }

    @SuppressLint("AutoDispose")
    private void initView() {
        nowPosition = getIntent().getIntExtra("position", 0);
        imgList.addAll((Collection<? extends String>) getIntent().getSerializableExtra("list"));
        TkFrontPhotosDetailAdapter tkFrontPhotosDetailAdapter = new TkFrontPhotosDetailAdapter(imgList);
        binding.pager.setAdapter(tkFrontPhotosDetailAdapter);
        binding.pager.setOffscreenPageLimit(10);
        binding.pager.setCurrentItem(nowPosition, false);

        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                nowPosition = position;
            }
        });

        binding.back.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("nowPosition", nowPosition);
            setResult(RESULT_OK, intent);
            finish();
        });

        tkFrontPhotosDetailAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (isHidden) {
                isHidden = false;
                binding.back.setVisibility(View.VISIBLE);
                binding.rlSet.setVisibility(View.VISIBLE);
                ImmersionBar.with(this)
                        .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                        .init();
            } else {
                isHidden = true;
                binding.back.setVisibility(View.GONE);
                binding.rlSet.setVisibility(View.GONE);
                ImmersionBar.with(this)
                        .hideBar(BarHide.FLAG_HIDE_BAR)
                        .init();
            }
        });

        binding.set.setOnClickListener(view -> {
            if (ClickUtil.isFastClick()) return;
            RxPermissions rxPermissions = new RxPermissions(TkFrontPhotosDetailActivity.this);
            rxPermissions
                    .request(Manifest.permission.SET_WALLPAPER)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            ToastyUtils.ToastShow(getString(R.string.tk_front_photos_setting));
                            Glide.with(TkFrontPhotosDetailActivity.this).asBitmap().load(imgList.get(nowPosition)).into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    try {
                                        WallpaperManager.getInstance(TkFrontPhotosDetailActivity.this).setBitmap(resource);
                                        ToastyUtils.ToastShow(getString(R.string.tk_front_photos_set_success));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        ToastyUtils.ToastShow(getString(R.string.tk_front_photos_set_faild));
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                        }
                    });
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("nowPosition", nowPosition);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}
