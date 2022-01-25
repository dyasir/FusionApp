package com.shortvideo.lib.ui.activity.concise.ncindex.interfaces;

import android.graphics.Bitmap;

/**
 * Created by Konstantin on 12.01.2015.
 */
public interface ScreenShotable {

    void takeScreenShot();

    void takeTopHeight();

    Bitmap getBitmap();

    int getTopHeight();
}
