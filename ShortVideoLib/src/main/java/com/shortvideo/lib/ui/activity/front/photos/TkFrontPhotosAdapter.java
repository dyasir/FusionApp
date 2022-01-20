package com.shortvideo.lib.ui.activity.front.photos;

import android.text.TextUtils;
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
import com.shortvideo.lib.model.WallpaperBean;
import com.shortvideo.lib.utils.SizeUtils;

import java.util.List;

public class TkFrontPhotosAdapter extends BaseQuickAdapter<WallpaperBean.ImagesDTO, BaseViewHolder> {

    public TkFrontPhotosAdapter(@Nullable List<WallpaperBean.ImagesDTO> data) {
        super(R.layout.tk_item_front_photos, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, WallpaperBean.ImagesDTO imagesDTO) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) baseViewHolder.getView(R.id.img).getLayoutParams();
        layoutParams.width = VideoApplication.getInstance().getFrontPhotosSpanCount() == 2 ? SizeUtils.dp2px(165f) :
                VideoApplication.getInstance().getFrontPhotosSpanCount() == 3 ? SizeUtils.dp2px(105f) : SizeUtils.dp2px(75f);
        layoutParams.height = VideoApplication.getInstance().getFrontPhotosSpanCount() == 2 ? SizeUtils.dp2px(375.5f) :
                VideoApplication.getInstance().getFrontPhotosSpanCount() == 3 ? SizeUtils.dp2px(227.5f) : SizeUtils.dp2px(162.5f);
        baseViewHolder.getView(R.id.img).setLayoutParams(layoutParams);

        RequestOptions options = new RequestOptions()
                .error(R.mipmap.tk_icon_front_photos_error)
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
