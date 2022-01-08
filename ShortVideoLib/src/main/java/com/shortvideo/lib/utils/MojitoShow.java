package com.shortvideo.lib.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import net.mikaelzero.mojito.Mojito;
import net.mikaelzero.mojito.impl.DefaultPercentProgress;
import net.mikaelzero.mojito.impl.NumIndicator;

import java.util.List;

import kotlin.Unit;

/**
 * 图片全屏查看
 * 手势拖拽消失
 */
public class MojitoShow {

    public static void singleView(Context context, String imageUrl, View view, boolean auto) {
        if (TextUtils.isEmpty(imageUrl)) {
            ToastyUtils.ToastShow("Lỗi hình ảnh");
            return;
        }
        Mojito.Companion.start(context, mojitoBuilder -> {
            mojitoBuilder.urls(imageUrl)
                    .views(view)
                    .autoLoadTarget(auto)
                    .progressLoader(DefaultPercentProgress::new);
            return Unit.INSTANCE;
        });
    }

    public static void recyclerView(Context context, RecyclerView recyclerView, int res, List<String> list, int position) {
        if (list == null || list.size() == 0) {
            ToastyUtils.ToastShow("Lỗi hình ảnh");
            return;
        }
        Mojito.Companion.start(context, mojitoBuilder -> {
            mojitoBuilder.views(recyclerView, res)
                    .urls(list)
                    .position(position)
                    .progressLoader(DefaultPercentProgress::new)
                    .setIndicator(new NumIndicator());
            return Unit.INSTANCE;
        });
    }
}
