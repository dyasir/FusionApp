package com.shortvideo.lib.ui.activity.front.photos;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.databinding.TkFragmentFrontPhotosMoreBinding;
import com.shortvideo.lib.model.WallpaperBean;
import com.shortvideo.lib.utils.MojitoShow;
import com.shortvideo.lib.utils.ScaleInTransformer;
import com.shortvideo.lib.utils.ScaleVerticalTransformer;
import com.shortvideo.lib.utils.SizeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TkFrontPhotosMoreFragment extends Fragment {

    private TkFragmentFrontPhotosMoreBinding binding;

    private TkFrontPhotosDetailAdapter tkFrontPhotosDetailAdapter;
    private final List<String> photoList = new ArrayList<>();
    private boolean firstLoad = true;
    private ActivityResultLauncher launcher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TkFragmentFrontPhotosMoreBinding.inflate(inflater, container, false);
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
        if (VideoApplication.getInstance().getFrontPageIndicatorLayoutType() == 1){
            lineParams.addRule(RelativeLayout.ALIGN_START, R.id.title);
        }else if (VideoApplication.getInstance().getFrontPageIndicatorLayoutType() == 2){
            lineParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }else {
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
        //viewpgaer间距
        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) binding.pager.getLayoutParams();
        if (VideoApplication.getInstance().getFrontPhotosScollType() == 1) {
            layoutParams1.setMargins(0, SizeUtils.dp2px(120f), 0, SizeUtils.dp2px(60f));
        } else {
            layoutParams1.setMargins(SizeUtils.dp2px(16f), SizeUtils.dp2px(60f), SizeUtils.dp2px(16f), 0);
        }
        binding.pager.setLayoutParams(layoutParams1);
        /** 自定义属性部分结束 **/

        tkFrontPhotosDetailAdapter = new TkFrontPhotosDetailAdapter(new ArrayList<>());
        binding.pager.setAdapter(tkFrontPhotosDetailAdapter);
        if (VideoApplication.getInstance().getFrontPhotosScollType() == 2)
            binding.pager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(VideoApplication.getInstance().getFrontPhotosScollType() == 1 ?
                new ScaleInTransformer() : new ScaleVerticalTransformer());
        compositePageTransformer.addTransformer(new MarginPageTransformer(SizeUtils.dp2px(16f)));
        binding.pager.setPageTransformer(compositePageTransformer);
        binding.pager.setOffscreenPageLimit(10);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null)
                binding.pager.setCurrentItem(result.getData().getIntExtra("nowPosition", 0), false);
        });

        tkFrontPhotosDetailAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (VideoApplication.getInstance().isApplyFrontPhotosWallpaper()) {
                Intent intent = new Intent(getActivity(), TkFrontPhotosDetailActivity.class);
                intent.putExtra("list", (Serializable) photoList);
                intent.putExtra("position", position);
                launcher.launch(intent);
            } else {
                MojitoShow.singleView(binding.pager.getContext(), tkFrontPhotosDetailAdapter.getData().get(position),
                        tkFrontPhotosDetailAdapter.getViewByPosition(position, R.id.img), false);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && firstLoad)
            initData();
    }

    private void initData() {
        HttpRequest.getHotWallpaper(this, VideoApplication.getInstance().getFrontPhotosLayoutType() == 1 ? 80 : 200, new HttpCallBack<WallpaperBean>() {
            @Override
            public void onSuccess(WallpaperBean wallpaperBean, String msg) {
                firstLoad = false;
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
                List<String> list = new ArrayList<>();
                for (WallpaperBean.ImagesDTO imagesDTO : wallpaperBean.getImages()) {
                    if (TextUtils.isEmpty(imagesDTO.getUplay())) {
                        if (TextUtils.isEmpty(imagesDTO.getImg())) {
                            list.add(imagesDTO.getOrigin());
                        } else {
                            list.add(imagesDTO.getImg());
                        }
                    } else {
                        list.add(imagesDTO.getUplay());
                    }
                }
                tkFrontPhotosDetailAdapter.setList(list);

                if (list.size() > 0) {
                    View view = binding.pager.getChildAt(0);
                    if (view instanceof RecyclerView) {
                        if (VideoApplication.getInstance().getFrontPhotosScollType() == 1) {
                            view.setPadding(200, 0, 200, 0);
                        } else {
                            view.setPadding(0, 200, 0, 200);
                        }
                        ((RecyclerView) view).setClipToPadding(false);
                    }
                }
            }

            @Override
            public void onFail(int errorCode, String errorMsg) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
