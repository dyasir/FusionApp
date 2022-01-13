package com.shortvideo.lib.ui.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.event.OnVideoDoubleLikeEvent;
import com.shortvideo.lib.common.event.OnVideoLongPressEvent;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.model.AdBean;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.TimeDateUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

public class StdTikTok extends StandardGSYVideoPlayer {

    private String link = "";
    private int bannerId = 0;
    private int bannerType = 1;
    private int position = -1;
    //    private ImageView before;
    private LottieAnimationView lottieView;
    private String thumUrl = "";
    private final float[] num = {-30, -20, -10, 0, 10, 20, 30};//随机心形图片角度
    private boolean canClick = true;

    //双击的时间
    private long doubleClickTime = 0;

    public StdTikTok(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public StdTikTok(Context context) {
        super(context);
    }

    public StdTikTok(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.tk_layout_empty_control;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
//        before = findViewById(R.id.before);
        lottieView = findViewById(R.id.lottieView);
        mProgressBar.setEnabled(false);
    }

    public void setProgresses(int progress) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mProgressBar.setProgress(progress, true);
        } else {
            mProgressBar.setProgress(progress);
        }
    }

    public void setProgressVisible(boolean visible) {
        canClick = visible;
        if (visible) {
            mProgressBar.setVisibility(VISIBLE);
        } else {
            mProgressBar.setVisibility(INVISIBLE);
        }
    }

    public void setBefore(String url) {
        thumUrl = url;
        if (TextUtils.isEmpty(url)) {
//            before.setVisibility(INVISIBLE);
            lottieView.setVisibility(INVISIBLE);
            mProgressBar.setVisibility(INVISIBLE);
        } else {
//            before.setVisibility(VISIBLE);
//            Glide.with(getContext())
//                    .load(url)
//                    .dontAnimate()
//                    .into(before);
            lottieView.setVisibility(VISIBLE);
            mProgressBar.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onInfo(int what, int extra) {
        super.onInfo(what, extra);
        //缓冲开始
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            //显示ProgressDialog
            lottieView.setVisibility(VISIBLE);
            lottieView.playAnimation();
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            //隐藏ProgressDialog
            lottieView.setVisibility(INVISIBLE);
            lottieView.cancelAnimation();
        }
    }

    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);

        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false;

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false;

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false;
    }

    @Override
    protected void touchDoubleUp(MotionEvent e) {
        //不需要双击
//        super.touchDoubleUp(e);
        if (!VideoApplication.getInstance().isPageScoll() && canClick) {
            if (TextUtils.isEmpty(link)) {
                if (position != -1)
                    EventBus.getDefault().post(new OnVideoDoubleLikeEvent(position));

                doubleClickTime = TimeDateUtils.getCurTimeLong();
                animalBegin(e.getX(), e.getY());
            } else {
                Log.e("result", "广告点击: " + link);
                Uri content_url = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
                getContext().startActivity(intent);
                reportBannerClick(bannerId, bannerType);
            }
        }
    }

    @Override
    protected void touchLongPress(MotionEvent e) {
//        super.touchLongPress(e);
        if (!VideoApplication.getInstance().isPageScoll() && canClick) {
            if (TextUtils.isEmpty(link)) {
                EventBus.getDefault().post(new OnVideoLongPressEvent());
            } else {
                Log.e("result", "广告点击: " + link);
                Uri content_url = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
                getContext().startActivity(intent);
                reportBannerClick(bannerId, bannerType);
            }
        }
    }

    @Override
    protected void updateStartImage() {
        setDismissControlTime(100_000_000);
        if (mStartButton instanceof ImageView) {
            ImageView imageView = (ImageView) mStartButton;
            if (mCurrentState == CURRENT_STATE_PAUSE) {
                imageView.setVisibility(VISIBLE);
            } else {
                imageView.setVisibility(INVISIBLE);
            }
//            if (mCurrentState == CURRENT_STATE_PLAYING) {
//                imageView.setVisibility(INVISIBLE);
//            } else {
//                imageView.setVisibility(VISIBLE);
//            }
        }

        if (!TextUtils.isEmpty(thumUrl) && (mCurrentState == CURRENT_STATE_NORMAL ||
                mCurrentState == CURRENT_STATE_PREPAREING)) {
            lottieView.setVisibility(VISIBLE);
            lottieView.playAnimation();
//            before.setVisibility(VISIBLE);
        } else {
            lottieView.setVisibility(INVISIBLE);
            lottieView.cancelAnimation();
//            before.setVisibility(INVISIBLE);
        }
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setBannerId(int id, int bannerType) {
        this.bannerId = id;
        this.bannerType = bannerType;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    protected void onClickUiToggle(MotionEvent e) {
        if (!VideoApplication.getInstance().isPageScoll() && canClick) {
            if (!TextUtils.isEmpty(link)) {
                Log.e("result", "广告点击: " + link);
                Uri content_url = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
                getContext().startActivity(intent);
                reportBannerClick(bannerId, bannerType);
            } else {
                if (TimeDateUtils.getCurTimeLong() - doubleClickTime < 800) {
                    doubleClickTime = TimeDateUtils.getCurTimeLong();
                    animalBegin(e.getX(), e.getY());
                } else {
                    Log.e("result", "视频点击: " + link);
                    super.onClickUiToggle(e);
                    mStartButton.performClick();
                }
            }
        }
    }

    @Override
    protected void touchSurfaceDown(float x, float y) {
        if (!VideoApplication.getInstance().isPageScoll()) {
            if (!TextUtils.isEmpty(link) && TimeDateUtils.getCurTimeLong() - doubleClickTime < 800) {
                doubleClickTime = TimeDateUtils.getCurTimeLong();
                animalBegin(x, y);
            } else {
                super.touchSurfaceDown(x, y);
            }
        }
    }

    /**
     * 开始点赞动画
     */
    private void animalBegin(float x, float y) {
        final ImageView imageView = new ImageView(mContext);
        LayoutParams params = new LayoutParams(200, 200);
        params.leftMargin = (int) x - 100;
        params.topMargin = (int) y - 200;
        imageView.setImageResource(R.mipmap.tk_im_heart);
        imageView.setLayoutParams(params);
        addView(imageView);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scale(imageView, "scaleX", 2f, 0.9f, 100, 0))
                .with(scale(imageView, "scaleY", 2f, 0.9f, 100, 0))
                .with(rotation(imageView, 0, 0, num[new Random().nextInt(6)]))
                .with(alpha(imageView, 0, 1, 100, 0))
                .with(scale(imageView, "scaleX", 0.9f, 1, 50, 150))
                .with(scale(imageView, "scaleY", 0.9f, 1, 50, 150))
                .with(translationY(imageView, 0, -600, 800, 400))
                .with(alpha(imageView, 1, 0, 300, 400))
                .with(scale(imageView, "scaleX", 1, 3f, 700, 400))
                .with(scale(imageView, "scaleY", 1, 3f, 700, 400));

        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeViewInLayout(imageView);
            }
        });
    }

    /**
     * 上报广告点击次数接口
     *
     * @param id   广告id
     * @param type 1.点击视频广告   2.点击图片广告   3.点击卡片广告
     */
    public void reportBannerClick(int id, int type) {
        if (id <= 0)
            return;
        Bundle bundle = new Bundle();
        bundle.putString("ad_Id", id + "");
        VideoApplication.getInstance().getmFirebaseAnalytics().logEvent(type == 1 ? "app_ad_video_click" :
                type == 2 ? "app_ad_img_click" : "app_ad_card_click", bundle);
        HttpRequest.adClick((AppCompatActivity) ActivityManager.getAppInstance().currentActivity(), id, new HttpCallBack<AdBean>() {
            @Override
            public void onSuccess(AdBean adBean, String msg) {

            }

            @Override
            public void onFail(int errorCode, String errorMsg) {

            }
        });
    }

    public static ObjectAnimator scale(View view, String propertyName, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , propertyName
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator translationX(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "translationX"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator translationY(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "translationY"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator alpha(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "alpha"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator rotation(View view, long time, long delayTime, float... values) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", values);
        rotation.setDuration(time);
        rotation.setStartDelay(delayTime);
        rotation.setInterpolator(input -> input);
        return rotation;
    }
}
