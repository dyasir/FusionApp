package com.shortvideo.lib.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.shortvideo.lib.R;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class TkEmptyControlVideo extends StandardGSYVideoPlayer {

    public TkEmptyControlVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public TkEmptyControlVideo(Context context) {
        super(context);
    }

    public TkEmptyControlVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.tk_empty_control_video;
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
        //super.touchDoubleUp();
        //不需要双击暂停
    }
}
