package com.shortvideo.lib.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.shortvideo.lib.R;

import java.util.List;

public class ReportAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ReportAdapter(@Nullable List<String> data) {
        super(R.layout.tk_item_report, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.name, s);
    }
}
