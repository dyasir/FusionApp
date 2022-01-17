package com.shortvideo.lib.ui.activity.front.mine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shortvideo.lib.R;
import com.shortvideo.lib.model.SelfieBean;

import java.io.File;
import java.util.List;

public class TkFrontMyVideoAdapter extends BaseQuickAdapter<SelfieBean, BaseViewHolder> {

    public TkFrontMyVideoAdapter(@Nullable List<SelfieBean> data) {
        super(R.layout.tk_item_front_my_video, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, SelfieBean selfieBean) {
        Glide.with(getContext())
                .load(new File(selfieBean.getVideoThum()))
                .into((RoundedImageView) baseViewHolder.getView(R.id.img));
    }
}
