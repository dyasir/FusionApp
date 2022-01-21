package com.shortvideo.lib.ui.activity.front.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.widgets.TipPop;
import com.shortvideo.lib.databinding.TkFragmentFrontMineBinding;
import com.shortvideo.lib.utils.DataCleanManager;
import com.shortvideo.lib.utils.SizeUtils;
import com.shortvideo.lib.utils.ToastyUtils;

public class TkFrontMineFragment extends Fragment implements View.OnClickListener {

    private TkFragmentFrontMineBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = TkFragmentFrontMineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initData();
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
        //版本号颜色
        binding.version.setTextColor(getResources().getColor(VideoApplication.getInstance().getFrontPageTitleColor()));
        //分割线颜色
        binding.line1.setBackgroundResource(VideoApplication.getInstance().getFrontPageBottomBgColor());
        binding.line2.setBackgroundResource(VideoApplication.getInstance().getFrontPageBottomBgColor());
        //去评分颜色
        binding.txRate.setTextColor(getResources().getColor(VideoApplication.getInstance().getFrontPageTitleColor()));
        //清除缓存颜色
        binding.txClean.setTextColor(getResources().getColor(VideoApplication.getInstance().getFrontPageTitleColor()));
        binding.cache.setTextColor(getResources().getColor(VideoApplication.getInstance().getFrontPageTitleColor()));
        binding.rlVideo.setVisibility(VideoApplication.getInstance().isApplyFrontHomeVideo() &&
                VideoApplication.getInstance().isApplyFrontPageTakeVideo() ? View.VISIBLE : View.GONE);
        binding.txVideo.setTextColor(getResources().getColor(VideoApplication.getInstance().getFrontPageTitleColor()));
        /** 自定义属性部分结束 **/
    }

    private void initData() {
        binding.rlJump.setOnClickListener(this);
        binding.rlClean.setOnClickListener(this);
        binding.rlVideo.setOnClickListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            binding.version.setText(getString(R.string.tk_setting_version, VideoApplication.getInstance().getVerName()));
            try {
                binding.cache.setText(DataCleanManager.getTotalCacheSize(binding.cache.getContext()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_jump) {
            openGooglePlay();
        } else if (v.getId() == R.id.rl_clean) {
            TipPop tipPop = new TipPop(binding.rlClean.getContext(), () -> {
                DataCleanManager.clearAllCache(binding.cache.getContext());
                try {
                    binding.cache.setText(DataCleanManager.getTotalCacheSize(binding.cache.getContext()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastyUtils.ToastShow(getString(R.string.tk_setting_cache_success));
            });
            tipPop.initTip(getString(R.string.tk_setting_cache_tip_title), getString(R.string.tk_setting_cache_tip));
        } else if (v.getId() == R.id.rl_video) {
            startActivity(new Intent(getActivity(), TkFrontMyVideoActivity.class));
        }
    }

    /**
     * 跳转谷歌市场对应应用
     */
    private void openGooglePlay() {
        try {
            Uri uri = Uri.parse("market://details?id=" + binding.rlJump.getContext().getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.android.vending");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + binding.rlJump.getContext().getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}