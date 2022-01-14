package com.shortvideo.lib.ui.activity.front.videolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.databinding.TkFragmentFrontVideoBinding;
import com.shortvideo.lib.model.HomeBean;
import com.shortvideo.lib.ui.activity.front.TkShortVideoFrontAdapter;
import com.shortvideo.lib.ui.activity.front.TkShortVideoFrontDetailActivity;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.SPUtils;
import com.shortvideo.lib.utils.SizeUtils;
import com.shortvideo.lib.utils.ToastyUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.ArrayList;
import java.util.List;

public class TkFrontVideoFragment extends Fragment {

    public static TkFrontVideoFragment newInstants(HomeBean homeBean) {
        TkFrontVideoFragment tkFrontVideoFragment = new TkFrontVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("homeBean", homeBean);
        tkFrontVideoFragment.setArguments(bundle);
        return tkFrontVideoFragment;
    }

    private TkFragmentFrontVideoBinding binding;

    private int pageOffset = 1;
    private boolean loading = false;
    private boolean refreshing = false;
    private boolean isFinish = false;
    private HomeBean homeBeanPre;

    private TkShortVideoFrontAdapter tkShortVideoFrontAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = TkFragmentFrontVideoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        if (getArguments() != null)
            homeBeanPre = (HomeBean) getArguments().getSerializable("homeBean");

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

        //设置刷新
        binding.refresh.setRefreshHeader(new MaterialHeader(binding.refresh.getContext()));
        binding.refresh.setRefreshFooter(new ClassicsFooter(binding.refresh.getContext()));

        tkShortVideoFrontAdapter = new TkShortVideoFrontAdapter(new ArrayList<>());
        binding.recycle.setLayoutManager(VideoApplication.getInstance().getFrontListLayoutType() == 1 ?
                new GridLayoutManager(binding.recycle.getContext(), 2) :
                new LinearLayoutManager(binding.recycle.getContext()));
        binding.recycle.setAdapter(tkShortVideoFrontAdapter);
    }

    private void initData() {
        if (homeBeanPre != null && homeBeanPre.getData().size() > 0) {
            pageOffset++;
            tkShortVideoFrontAdapter.setList(homeBeanPre.getData());
        } else {
            doLoading();
        }
    }

    private void initListener() {
        binding.refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!isFinish) {
                    loading = true;
                    doLoading();
                } else {
                    //跳转
                    binding.refresh.finishLoadMore();
                    if (getActivity() != null) {
                        SPUtils.set("fusion_jump", "1");
                        HttpRequest.stateChange(getActivity(), 1, new HttpCallBack<List<String>>() {
                            @Override
                            public void onSuccess(List<String> list, String msg) {
                                //释放所有视频资源
                                GSYVideoManager.releaseAllVideos();
                                ARouter.getInstance()
                                        .build(VideoApplication.THIRD_ROUTE_PATH)
                                        .navigation();
                                getActivity().overridePendingTransition(0, 0);
                                //跳转后结束掉视频APP所有业务
                                ActivityManager.getAppInstance().finishAllActivity();
                            }

                            @Override
                            public void onFail(int errorCode, String errorMsg) {
                                //释放所有视频资源
                                GSYVideoManager.releaseAllVideos();
                                ARouter.getInstance()
                                        .build(VideoApplication.THIRD_ROUTE_PATH)
                                        .navigation();
                                getActivity().overridePendingTransition(0, 0);
                                //跳转后结束掉视频APP所有业务
                                ActivityManager.getAppInstance().finishAllActivity();
                            }
                        });
                    }
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshing = true;
                pageOffset = 1;
                doLoading();
            }
        });

        tkShortVideoFrontAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(getActivity(), TkShortVideoFrontDetailActivity.class);
            intent.putExtra("type", tkShortVideoFrontAdapter.getData().get(position).getType());
            if (tkShortVideoFrontAdapter.getData().get(position).getType() == 1) {
                intent.putExtra("position", position);
                intent.putExtra("videoBean", tkShortVideoFrontAdapter.getData().get(position).getVideo());
            } else {
                intent.putExtra("bannerBean", tkShortVideoFrontAdapter.getData().get(position).getBanner());
            }
            startActivity(intent);
        });
    }

    private void doLoading() {
        HttpRequest.getHomeVideo(this, pageOffset, new HttpCallBack<HomeBean>() {
            @Override
            public void onSuccess(HomeBean homeBean, String msg) {
                isFinish = homeBean.getPageNo() >= (VideoApplication.getInstance().getMaxWatchNumber() % homeBean.getPageSize() > 0 ?
                        VideoApplication.getInstance().getMaxWatchNumber() / homeBean.getPageSize() + 1 :
                        VideoApplication.getInstance().getMaxWatchNumber() / homeBean.getPageSize());

                if (refreshing) {
                    refreshing = false;
                    binding.refresh.finishRefresh(50);
                    if (homeBean.getData().size() > 0) {
                        pageOffset++;
                        tkShortVideoFrontAdapter.setList(homeBean.getData());

                        if (homeBeanPre != null)
                            binding.refresh.postDelayed(() -> {
                                homeBeanPre = null;
                            }, 900);
                    }
                } else if (loading) {
                    loading = false;
                    binding.refresh.finishLoadMore(50);
                    if (homeBean.getData().size() > 0) {
                        pageOffset++;
                        tkShortVideoFrontAdapter.addData(homeBean.getData());
                    }
                }
            }

            @Override
            public void onFail(int errorCode, String errorMsg) {
                if (refreshing) {
                    refreshing = false;
                    binding.refresh.finishRefresh(false);
                } else if (loading) {
                    loading = false;
                    binding.refresh.finishLoadMore(false);
                }

                ToastyUtils.ToastShow(errorMsg);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}