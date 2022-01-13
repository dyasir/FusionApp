package com.shortvideo.lib.ui.widgets;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;

import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.databinding.TkPopShareBinding;
import com.shortvideo.lib.model.DataMgr;
import com.shortvideo.lib.utils.ToastyUtils;

import razerdp.basepopup.BasePopupWindow;
import razerdp.util.animation.AnimationHelper;
import razerdp.util.animation.TranslationConfig;

public class SharePop extends BasePopupWindow implements View.OnClickListener {

    TkPopShareBinding binding;

    private final int id;

    public SharePop(Context context, int id) {
        super(context);
        this.id = id;

        binding = TkPopShareBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        setOutSideDismiss(true);
        setPopupGravity(Gravity.BOTTOM);
    }

    @Override
    public void onViewCreated(@NonNull View contentView) {
        super.onViewCreated(contentView);

        binding.facebook.setOnClickListener(this);
        binding.twitter.setOnClickListener(this);
        binding.zalo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        if (v.getId() == R.id.facebook) {
            bundle = new Bundle();
            bundle.putString("app_package_id", getContext().getPackageName());
            VideoApplication.getInstance().getmFirebaseAnalytics().logEvent("app_share_to_facebook", bundle);
            jumpToShare(1, "fb");
        } else if (v.getId() == R.id.twitter) {
            bundle = new Bundle();
            bundle.putString("app_package_id", getContext().getPackageName());
            VideoApplication.getInstance().getmFirebaseAnalytics().logEvent("app_share_to_twitter", bundle);
            jumpToShare(2, "tw");
        } else if (v.getId() == R.id.zalo) {
            bundle = new Bundle();
            bundle.putString("app_package_id", getContext().getPackageName());
            VideoApplication.getInstance().getmFirebaseAnalytics().logEvent("app_share_to_zalo", bundle);
            jumpToShare(3, "zalo");
        }
    }

    /**
     * 原生分享到哪里(暂时分享文字链接，视频待调研)
     *
     * @param type 1.Facebook   2.Twitter   3.Zalo
     */
    private void jumpToShare(int type, String f) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(type == 1 ? "com.facebook.katana" : type == 2 ? "com.twitter.android" : "com.zing.zalo");
            intent.putExtra(Intent.EXTRA_TEXT, DataMgr.getInstance().getUser().getVideo_share().replaceAll("\\[url]",
                    DataMgr.getInstance().getUser().getVideo_view_domian() + "/share?id=" + id + "&f=" + f));
            intent.setType("text/plain");
            getContext().startActivity(Intent.createChooser(intent, getContext().getString(R.string.tk_share_pop_title)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastyUtils.ToastShow(getContext().getString(R.string.tk_share_pop_unstall));
        }
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return AnimationHelper.asAnimation()
                .withTranslation(TranslationConfig.FROM_BOTTOM)
                .toShow();
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return AnimationHelper.asAnimation()
                .withTranslation(TranslationConfig.TO_BOTTOM)
                .toShow();
    }
}
