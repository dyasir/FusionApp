package com.shortvideo.lib.ui.activity.front.photos;

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

import java.util.List;

public class TkFrontPhotosDetailAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public TkFrontPhotosDetailAdapter(@Nullable List<String> data) {
        super(R.layout.tk_item_front_photos_detail, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        RoundedImageView img = baseViewHolder.getView(R.id.img);
        if (VideoApplication.getInstance().getFrontPhotosLayoutType() == 2)
            img.setCornerRadius(16f);

        RequestOptions options = new RequestOptions()
                .error(R.mipmap.tk_icon_front_photos_error)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        Glide.with(getContext())
                .load(s)
                .thumbnail(0.1f)
                .apply(options)
                .into(img);
    }
}
