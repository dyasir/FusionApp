package com.shortvideo.lib.ui.activity.front.photos;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.scwang.smart.refresh.header.MaterialHeader;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.databinding.TkFragmentFrontPhotosBinding;
import com.shortvideo.lib.model.WallpaperBean;
import com.shortvideo.lib.utils.MojitoShow;
import com.shortvideo.lib.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

public class TkFrontPhotosFragment extends Fragment {

    private TkFragmentFrontPhotosBinding binding;

    private TkFrontPhotosAdapter tkFrontPhotosAdapter;
    private final List<String> photoList = new ArrayList<>();
    private boolean isRefresh = false;
    private boolean firstLoad = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TkFragmentFrontPhotosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    private void initView() {
        /** 自定义属性部分开始 **/
        //背景色
        binding.getRoot().setBackgroundResource(VideoApplication.getInstance().getFrontPageBgColor());
        //标题颜色以及字号
        binding.title.setTextColor(getResources().getColor(VideoApplication.getInstance().getFrontPageTitleColor()));
        binding.title.setTextSize(VideoApplication.getInstance().getFrontPageTitleSize());
        //标题下划线圆角、颜色、宽高
        binding.line.getShapeBuilder().setShapeCornersRadius(VideoApplication.getInstance().getFrontPageIndicatorCornersRadius())
                .setShapeSolidColor(getResources().getColor(VideoApplication.getInstance().getFrontPageIndicatorColor()))
                .into(binding.line);
        ViewGroup.LayoutParams layoutParams = binding.line.getLayoutParams();
        layoutParams.width = SizeUtils.dp2px(VideoApplication.getInstance().getFrontPageIndicatorWidth());
        layoutParams.height = SizeUtils.dp2px(VideoApplication.getInstance().getFrontPageIndicatorHeight());
        binding.line.setLayoutParams(layoutParams);
        /** 自定义属性部分结束 **/

        tkFrontPhotosAdapter = new TkFrontPhotosAdapter(new ArrayList<>());
        binding.recycle.setLayoutManager(new GridLayoutManager(binding.recycle.getContext(), 2));
        binding.recycle.setAdapter(tkFrontPhotosAdapter);

        binding.refresh.setRefreshHeader(new MaterialHeader(binding.refresh.getContext()));
        binding.refresh.setEnableLoadMore(false);
        binding.refresh.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            initData();
        });

        tkFrontPhotosAdapter.setOnItemClickListener((adapter, view, position) -> {
            MojitoShow.recyclerView(binding.recycle.getContext(), binding.recycle, R.id.img, photoList, position);
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && firstLoad)
            initData();
    }

    private void initData() {
        HttpRequest.getHotWallpaper(this, 200, new HttpCallBack<WallpaperBean>() {
            @Override
            public void onSuccess(WallpaperBean wallpaperBean, String msg) {
                firstLoad = false;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
