package com.shortvideo.lib.ui.activity.concise.ncindex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shortvideo.lib.R;

import java.io.File;
import java.util.List;

public class NcPhotosAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public NcPhotosAdapter(@Nullable List<String> data) {
        super(R.layout.tk_item_ncindex_photos, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        Glide.with(getContext())
                .load(new File(s))
                .into((RoundedImageView) baseViewHolder.getView(R.id.img));
    }
}
