package com.shortvideo.lib.ui.activity.concise.ncindex;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.noober.background.drawable.DrawableCreator;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.databinding.TkFragmentNcindexBinding;
import com.shortvideo.lib.ui.activity.TkShortVideoActivity;
import com.shortvideo.lib.ui.activity.TkShortVideoTwoActivity;
import com.shortvideo.lib.ui.activity.concise.ncindex.interfaces.ScreenShotable;
import com.shortvideo.lib.ui.activity.concise.photoedit.IMGGalleryActivity;
import com.shortvideo.lib.ui.activity.front.photos.TkFrontPhotosDetailActivity;
import com.shortvideo.lib.utils.ToastyUtils;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.IOException;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class NcIndexFragment extends Fragment implements ScreenShotable {

    private TkFragmentNcindexBinding binding;

    public static final String CLOSE = "Close";
    public static final String VIDEO = "Video";
    public static final String WALLPAPER = "Wallpaper";
    public static final String PHOTOS = "Photos";
    public static final String MESSAGE = "Message";
    public static final String MINE = "Mine";

    private String name;
    private Bitmap bitmap;
    private int topHeight;

    public static NcIndexFragment newInstance(String name) {
        NcIndexFragment ncIndexFragment = new NcIndexFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        ncIndexFragment.setArguments(bundle);
        return ncIndexFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString("name");
        } else {
            name = VIDEO;
        }

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Aclonica-Regular-2.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()
                )).build());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TkFragmentNcindexBinding.inflate(inflater, container, false);
        binding.getRoot().setBackgroundResource(VideoApplication.getInstance().getNcIndexBgColor());
        binding.title.setTextColor(getResources().getColor(VideoApplication.getInstance().getNcIndexTitleColor()));
        binding.title.setText(name.equals(VIDEO) ? "VIDEO" : name.equals(WALLPAPER) ? "WALLPAPER" : name.equals(PHOTOS) ? "PHOTOS" :
                name.equals(MESSAGE) ? "MESSAGE" : "MINE");
        binding.oneTx.setText(name.equals(VIDEO) ? "LIKE" : name.equals(WALLPAPER) ? "ULTRA-CLERA" : name.equals(PHOTOS) ? "GRAFFITI" :
                name.equals(MESSAGE) ? "TO CHAT" : "SELFIE");
        binding.twoTx.setText(name.equals(VIDEO) ? "DOWNLOAD" : name.equals(WALLPAPER) ? "QUICK SETUP" : name.equals(PHOTOS) ? "MOSAIC" :
                name.equals(MESSAGE) ? "EXPRESSION" : "SCORE");
        binding.oneImg.setImageResource(name.equals(VIDEO) ? R.mipmap.tk_icon_ncindex_video_one : name.equals(WALLPAPER) ?
                R.mipmap.tk_icon_ncindex_wallpaper_one : name.equals(PHOTOS) ? R.mipmap.tk_icon_ncindex_photos_one : name.equals(MESSAGE) ?
                R.mipmap.tk_icon_ncindex_message_one : R.mipmap.tk_icon_ncindex_mine_one);
        binding.twoImg.setImageResource(name.equals(VIDEO) ? R.mipmap.tk_icon_ncindex_video_two : name.equals(WALLPAPER) ?
                R.mipmap.tk_icon_ncindex_wallpaper_two : name.equals(PHOTOS) ? R.mipmap.tk_icon_ncindex_photos_two : name.equals(MESSAGE) ?
                R.mipmap.tk_icon_ncindex_message_two : R.mipmap.tk_icon_ncindex_mine_two);
        Drawable drawableOne = new DrawableCreator.Builder()
                .setCornersRadius(60)
                .setSolidColor(name.equals(VIDEO) ? getResources().getColor(R.color.color_introduce_video_one) :
                        name.equals(WALLPAPER) ? getResources().getColor(R.color.color_introduce_wallpaper_one) :
                                name.equals(PHOTOS) ? getResources().getColor(R.color.color_introduce_photos_one) :
                                        name.equals(MESSAGE) ? getResources().getColor(R.color.color_introduce_message_one) :
                                                getResources().getColor(R.color.color_introduce_mine_one))
                .build();
        binding.llOne.setBackground(drawableOne);
        Drawable drawableTwo = new DrawableCreator.Builder()
                .setCornersRadius(60)
                .setSolidColor(name.equals(VIDEO) ? getResources().getColor(R.color.color_introduce_video_two) :
                        name.equals(WALLPAPER) ? getResources().getColor(R.color.color_introduce_wallpaper_two) :
                                name.equals(PHOTOS) ? getResources().getColor(R.color.color_introduce_photos_two) :
                                        name.equals(MESSAGE) ? getResources().getColor(R.color.color_introduce_message_two) :
                                                getResources().getColor(R.color.color_introduce_mine_two))
                .build();
        binding.llTwo.setBackground(drawableTwo);
        Drawable drawableStart = new DrawableCreator.Builder()
                .setCornersRadius(60)
                .setSolidColor(name.equals(VIDEO) ? getResources().getColor(R.color.color_introduce_video_start) :
                        name.equals(WALLPAPER) ? getResources().getColor(R.color.color_introduce_wallpaper_start) :
                                name.equals(PHOTOS) ? getResources().getColor(R.color.color_introduce_photos_start) :
                                        name.equals(MESSAGE) ? getResources().getColor(R.color.color_introduce_message_start) :
                                                getResources().getColor(R.color.color_introduce_mine_start))
                .build();
        binding.start.setBackground(drawableStart);
        binding.oneTx.setTextColor(name.equals(VIDEO) ? getResources().getColor(R.color.color_introduce_video_one_tx) :
                name.equals(WALLPAPER) ? getResources().getColor(R.color.color_introduce_wallpaper_one_tx) :
                        name.equals(PHOTOS) ? getResources().getColor(R.color.color_introduce_photos_one_tx) :
                                name.equals(MESSAGE) ? getResources().getColor(R.color.color_introduce_message_one_tx) :
                                        getResources().getColor(R.color.color_introduce_mine_one_tx));
        binding.twoTx.setTextColor(name.equals(VIDEO) ? getResources().getColor(R.color.color_introduce_video_two_tx) :
                name.equals(WALLPAPER) ? getResources().getColor(R.color.color_introduce_wallpaper_two_tx) :
                        name.equals(PHOTOS) ? getResources().getColor(R.color.color_introduce_photos_two_tx) :
                                name.equals(MESSAGE) ? getResources().getColor(R.color.color_introduce_messageo_two_tx) :
                                        getResources().getColor(R.color.color_introduce_mine_two_tx));
        binding.start.setTextColor(name.equals(VIDEO) ? getResources().getColor(R.color.color_introduce_video_start_tx) :
                name.equals(WALLPAPER) ? getResources().getColor(R.color.color_introduce_wallpaper_start_tx) :
                        name.equals(PHOTOS) ? getResources().getColor(R.color.color_introduce_photos_start_tx) :
                                name.equals(MESSAGE) ? getResources().getColor(R.color.color_introduce_message_start_tx) :
                                        getResources().getColor(R.color.color_introduce_mine_start_tx));

        return binding.getRoot();
    }

    @SuppressLint("AutoDispose")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.start.setOnClickListener(view1 -> {
            if (name.equals(VIDEO)) {
                startActivity(new Intent(getActivity(), VideoApplication.getInstance().getVideoLayoutType() == 1 ?
                        TkShortVideoActivity.class : TkShortVideoTwoActivity.class));
            } else if (name.equals(WALLPAPER)) {
                startActivity(new Intent(getActivity(), NcWallpaperActivity.class));
            } else if (name.equals(PHOTOS)) {
                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                startActivity(new Intent(getActivity(), NcPhotosActivity.class));
                            }
                        });
            } else if (name.equals(MESSAGE)) {
                startActivity(new Intent(getActivity(), NcMessageActivity.class));
            } else {
                startActivity(new Intent(getActivity(), NcMineActivity.class));
            }
        });
    }

    @Override
    public void takeScreenShot() {
        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(binding.llContent.getWidth(),
                        binding.llContent.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                binding.llContent.draw(canvas);
                NcIndexFragment.this.bitmap = bitmap;
            }
        }.start();
    }

    @Override
    public void takeTopHeight() {
        new Thread() {
            @Override
            public void run() {
                NcIndexFragment.this.topHeight = binding.head.getHeight() + binding.title.getHeight();
            }
        }.start();
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public int getTopHeight() {
        return topHeight;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
