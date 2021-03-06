package com.shortvideo.lib.ui.activity.front.message;

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
import com.shortvideo.lib.databinding.TkFragmentFrontMessageBinding;
import com.shortvideo.lib.utils.SizeUtils;

public class TkFrontMessageFragment extends Fragment {

    private TkFragmentFrontMessageBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = TkFragmentFrontMessageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        //消息标题颜色
        binding.messageTitle.setTextColor(getResources().getColor(VideoApplication.getInstance().getFrontPageTitleColor()));
        //消息颜色
        binding.message.setTextColor(getResources().getColor(VideoApplication.getInstance().getFrontPageTitleColor()));
        /** 自定义属性部分结束 **/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}