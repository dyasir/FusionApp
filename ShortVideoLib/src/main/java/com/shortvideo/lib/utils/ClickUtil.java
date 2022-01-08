package com.shortvideo.lib.utils;

public class ClickUtil {

    private static long mLastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        long deltaTime = time - mLastClickTime;
        if (0 < deltaTime && deltaTime < 800) {
            return true;
        }
        mLastClickTime = time;

        return false;
    }

}
