package com.shortvideo.lib.ui.widgets;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.shortvideo.lib.databinding.PopOutSideVideoBinding;

import razerdp.basepopup.BasePopupWindow;

public class OutsidePop extends BasePopupWindow {

    PopOutSideVideoBinding binding;

    private final String str;

    public OutsidePop(Context context, String str) {
        super(context);
        this.str = str;

        binding = PopOutSideVideoBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        setOutSideDismiss(false);
        setPopupGravity(Gravity.CENTER);
    }

    @Override
    public void onViewCreated(@NonNull View contentView) {
        super.onViewCreated(contentView);

        binding.name.setText(str);
    }
}
