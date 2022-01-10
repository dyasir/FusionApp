package com.shortvideo.lib.ui.adapter;

import android.text.TextUtils;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shortvideo.lib.R;
import com.shortvideo.lib.utils.SizeUtils;

import java.util.List;

public class ReportDetailAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ReportDetailAdapter(@Nullable List<String> data) {
        super(R.layout.tk_item_report_detail, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        RelativeLayout rootview = baseViewHolder.getView(R.id.rootview);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) rootview.getLayoutParams();
        if (baseViewHolder.getLayoutPosition() % 4 == 3) {
            layoutParams.setMargins(0, 0, 0, 0);
        } else {
            layoutParams.setMargins(0, 0, SizeUtils.dp2px(5f), 0);
        }
        rootview.setLayoutParams(layoutParams);

        if (!TextUtils.isEmpty(s))
            Glide.with(getContext())
                    .load(s)
                    .dontAnimate()
                    .into((RoundedImageView) baseViewHolder.getView(R.id.img));

        baseViewHolder.setText(R.id.number, Math.max(0, getData().size() - 1) + "/4")
                .setGone(R.id.img, getData().size() == 0 || (getData().size() == 1 && TextUtils.isEmpty(s)) ||
                        (getData().size() > 1 && baseViewHolder.getLayoutPosition() == getData().size() - 1 && TextUtils.isEmpty(s)))
                .setGone(R.id.rl_close, getData().size() == 0 || (getData().size() == 1 && TextUtils.isEmpty(s)) ||
                        (getData().size() > 1 && baseViewHolder.getLayoutPosition() == getData().size() - 1 && TextUtils.isEmpty(s)))
                .setGone(R.id.ll_photo, !TextUtils.isEmpty(s));
    }
}
