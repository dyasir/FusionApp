package com.shortvideo.lib.ui.activity.front;

import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.model.HomeBean;
import com.shortvideo.lib.utils.SizeUtils;

import java.util.List;

public class TkShortVideoFrontAdapter extends BaseQuickAdapter<HomeBean.DataDTO, BaseViewHolder> {

    public TkShortVideoFrontAdapter(@Nullable List<HomeBean.DataDTO> data) {
        super(VideoApplication.getInstance().getFrontListLayoutType() == 1 ?
                R.layout.tk_item_short_video_front : R.layout.tk_item_short_video_front_vertical, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, HomeBean.DataDTO dataDTO) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        if (VideoApplication.getInstance().getFrontListLayoutType() == 1) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) baseViewHolder.getView(R.id.img).getLayoutParams();
            layoutParams.height = SizeUtils.dp2px(VideoApplication.getInstance().getFrontListItemHeight());
            baseViewHolder.getView(R.id.img).setLayoutParams(layoutParams);
        }

        if (dataDTO.getType() == 1) {
            Glide.with(getContext())
                    .load(dataDTO.getVideo().getThumburl())
                    .dontAnimate()
                    .apply(options)
                    .thumbnail(0.1f)
                    .into((RoundedImageView) baseViewHolder.getView(R.id.img));
        } else {
            if (dataDTO.getBanner().getBanner_type() == 2) {
                Glide.with(getContext())
                        .load(dataDTO.getBanner().getUrl())
                        .dontAnimate()
                        .apply(options)
                        .thumbnail(0.1f)
                        .into((RoundedImageView) baseViewHolder.getView(R.id.img));
            } else {
                Glide.with(getContext())
                        .load(dataDTO.getBanner().getIcon())
                        .dontAnimate()
                        .apply(options)
                        .thumbnail(0.1f)
                        .into((RoundedImageView) baseViewHolder.getView(R.id.img));
            }
        }

        baseViewHolder.setGone(R.id.look, !VideoApplication.getInstance().isApplyFrontLikeNum() || dataDTO.getType() != 1)
                .setText(R.id.look, dataDTO.getType() == 1 ? dataDTO.getVideo().getLike_count() + "" : "");
    }
}
