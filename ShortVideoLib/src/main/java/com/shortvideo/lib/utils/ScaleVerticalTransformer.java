package com.shortvideo.lib.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class ScaleVerticalTransformer implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {
        page.setElevation(-Math.abs(position));

        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        page.setPivotY(pageHeight / 2f);
        page.setPivotX(pageWidth / 2f);
        if (position < -1) {
            page.setScaleX(0.85f);
            page.setScaleY(0.85f);
            page.setPivotY(pageHeight * 1.0f);
        } else if (position <= 1) {
            if (position < 0) {
                float scaleFactor = (1 + position) * (1 - 0.85f) + 0.85f;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setPivotY(pageHeight * (0.5f + 0.5f * -position));
            } else {
                float scaleFactor = (1 - position) * (1 - 0.85f) + 0.85f;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setPivotY(pageHeight * ((1 - position) * 0.5f));
            }
        } else {
            page.setPivotY(0);
            page.setScaleX(0.85f);
            page.setScaleY(0.85f);
        }
    }
}
