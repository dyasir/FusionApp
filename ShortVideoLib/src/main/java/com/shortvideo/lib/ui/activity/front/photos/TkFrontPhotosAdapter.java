package com.shortvideo.lib.ui.activity.front.photos;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shortvideo.lib.R;
import com.shortvideo.lib.model.WallpaperBean;

import java.util.List;

public class TkFrontPhotosAdapter extends BaseQuickAdapter<WallpaperBean.ImagesDTO, BaseViewHolder> {

    public TkFrontPhotosAdapter(@Nullable List<WallpaperBean.ImagesDTO> data) {
        super(R.layout.tk_item_front_photos, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, WallpaperBean.ImagesDTO imagesDTO) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        Glide.with(getContext())
                .load(TextUtils.isEmpty(imagesDTO.getUplay()) ?
                        TextUtils.isEmpty(imagesDTO.getImg()) ? imagesDTO.getOrigin() :
                                imagesDTO.getImg() : imagesDTO.getUplay())
                .thumbnail(0.1f)
                .apply(options)
                .into((RoundedImageView) baseViewHolder.getView(R.id.img));
    }
}
