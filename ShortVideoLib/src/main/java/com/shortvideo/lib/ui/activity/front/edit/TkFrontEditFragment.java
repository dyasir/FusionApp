package com.shortvideo.lib.ui.activity.front.edit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.TkAppConfig;
import com.shortvideo.lib.databinding.TkFragmentFrontEditBinding;
import com.shortvideo.lib.ui.activity.concise.ncindex.NcPhotosAdapter;
import com.shortvideo.lib.ui.activity.concise.photoedit.IMGGalleryActivity;
import com.shortvideo.lib.utils.MojitoShow;
import com.shortvideo.lib.utils.SizeUtils;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TkFrontEditFragment extends Fragment {

    private TkFragmentFrontEditBinding binding;

    private NcPhotosAdapter ncPhotosAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TkFragmentFrontEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initListener();
    }

    private void initView() {
        /** 自定义属性部分开始 **/
        //背景色
        binding.getRoot().setBackgroundResource(VideoApplication.getInstance().getFrontPageBgColor());
        //标题相对位置
        RelativeLayout.LayoutParams headLayoutParams = (RelativeLayout.LayoutParams) binding.rlHead.getLayoutParams();
        if (VideoApplication.getInstance().getFrontPageTitleLayoutType() == 1) {
            headLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            headLayoutParams.setMargins(SizeUtils.dp2px(16f), SizeUtils.dp2px(16f), 0, 0);
        } else if (VideoApplication.getInstance().getFrontPageTitleLayoutType() == 2) {
            headLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            headLayoutParams.setMargins(0, SizeUtils.dp2px(16f), 0, 0);
        } else {
            headLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            headLayoutParams.setMargins(0, SizeUtils.dp2px(16f), SizeUtils.dp2px(16f), 0);
        }
        binding.rlHead.setLayoutParams(headLayoutParams);
        //标题颜色以及字号
        binding.title.setVisibility(VideoApplication.getInstance().isApplyFrontPageTitle() ? View.VISIBLE : View.GONE);
        binding.title.setTextColor(getResources().getColor(VideoApplication.getInstance().getFrontPageTitleColor()));
        binding.title.setTextSize(VideoApplication.getInstance().getFrontPageTitleSize());
        //标题下划线圆角、颜色、宽高
        RelativeLayout.LayoutParams lineParams = (RelativeLayout.LayoutParams) binding.line.getLayoutParams();
        if (VideoApplication.getInstance().getFrontPageIndicatorLayoutType() == 1) {
            lineParams.addRule(RelativeLayout.ALIGN_START, R.id.title);
        } else if (VideoApplication.getInstance().getFrontPageIndicatorLayoutType() == 2) {
            lineParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else {
            lineParams.addRule(RelativeLayout.ALIGN_END, R.id.title);
        }
        binding.line.setLayoutParams(lineParams);
        binding.line.setVisibility(VideoApplication.getInstance().isApplyFrontPageTitle() &&
                VideoApplication.getInstance().isApplyFrontPageIndicator() ? View.VISIBLE : View.GONE);
        binding.line.getShapeBuilder().setShapeCornersRadius(VideoApplication.getInstance().getFrontPageIndicatorCornersRadius())
                .setShapeSolidColor(getResources().getColor(VideoApplication.getInstance().getFrontPageIndicatorColor()))
                .into(binding.line);
        ViewGroup.LayoutParams layoutParams = binding.line.getLayoutParams();
        layoutParams.width = SizeUtils.dp2px(VideoApplication.getInstance().getFrontPageIndicatorWidth());
        layoutParams.height = SizeUtils.dp2px(VideoApplication.getInstance().getFrontPageIndicatorHeight());
        binding.line.setLayoutParams(layoutParams);
        /** 自定义属性部分结束 **/

        ncPhotosAdapter = new NcPhotosAdapter(new ArrayList<>());
        binding.recycle.setLayoutManager(new GridLayoutManager(binding.recycle.getContext(), 2));
        binding.recycle.setAdapter(ncPhotosAdapter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            initData();
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
        path.add(0, "");
        ncPhotosAdapter.setList(path);
    }

    @SuppressLint("AutoDispose")
    private void initListener() {
        ncPhotosAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (position == 0) {
                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                startActivity(new Intent(getActivity(), IMGGalleryActivity.class));
                            }
                        });
            } else {
                MojitoShow.recyclerView(binding.recycle.getContext(), binding.recycle, R.id.img, ncPhotosAdapter.getData(), position);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
