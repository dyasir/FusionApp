package com.shortvideo.lib.common.widgets;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.shortvideo.lib.R;
import com.shortvideo.lib.databinding.TkPopTipBinding;
import com.shortvideo.lib.ui.callback.OnTipPopCallback;

import razerdp.basepopup.BasePopupWindow;

public class TipPop extends BasePopupWindow implements View.OnClickListener {

    TkPopTipBinding binding;

    private final OnTipPopCallback onTipPopCallback;

    public TipPop(Context context, OnTipPopCallback onTipPopCallback) {
        super(context);
        this.onTipPopCallback = onTipPopCallback;

        binding = TkPopTipBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        setOutSideDismiss(false);
        setPopupGravity(Gravity.CENTER);
    }

    @Override
    public void onViewCreated(@NonNull View contentView) {
        super.onViewCreated(contentView);

        binding.sure.setOnClickListener(this);
        binding.cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sure) {
            dismiss();
            if (onTipPopCallback != null)
                onTipPopCallback.onTipPopSure();
        } else if (v.getId() == R.id.cancel) {
            dismiss();
        }
    }

    public void initTip(String title, String content) {
        binding.title.setText(title);
        binding.content.setText(content);

        showPopupWindow();
    }
}
