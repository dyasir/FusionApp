package com.shortvideo.lib.ui.activity.front;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shortvideo.lib.R;
import com.shortvideo.lib.model.HomeBean;

import java.util.List;

public class TkShortVideoFrontAdapter extends BaseQuickAdapter<HomeBean.DataDTO, BaseViewHolder> {

    public TkShortVideoFrontAdapter(@Nullable List<HomeBean.DataDTO> data) {
        super(R.layout.tk_item_short_video_front, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, HomeBean.DataDTO dataDTO) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        if (dataDTO.getType() == 1) {
            Glide.with(getContext())
                    .load(dataDTO.getVideo().getThumburl())
                    .dontAnimate()
                    .apply(options)
                    .into((RoundedImageView) baseViewHolder.getView(R.id.img));
        } else {
            if (dataDTO.getBanner().getBanner_type() == 2) {
                Glide.with(getContext())
                        .load(dataDTO.getBanner().getUrl())
                        .dontAnimate()
                        .apply(options)
                        .into((RoundedImageView) baseViewHolder.getView(R.id.img));
            } else {
                Glide.with(getContext())
                        .load(dataDTO.getBanner().getIcon())
                        .dontAnimate()
                        .apply(options)
                        .into((RoundedImageView) baseViewHolder.getView(R.id.img));
            }
        }
    }
}
