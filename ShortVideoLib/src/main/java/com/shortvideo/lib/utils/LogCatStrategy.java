package com.shortvideo.lib.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.LogStrategy;

import org.jetbrains.annotations.NotNull;

public class LogCatStrategy implements LogStrategy {

    private final Handler handler;
    private long lastTime = SystemClock.uptimeMillis();

    public LogCatStrategy() {
        HandlerThread thread = new HandlerThread("thread_print");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    @Override
    public void log(int priority, @Nullable @org.jetbrains.annotations.Nullable String tag, @NonNull @NotNull String message) {
        long offset = 5;
        lastTime += offset;
        if (lastTime < SystemClock.uptimeMillis()) {
            lastTime = SystemClock.uptimeMillis() + offset;
        }
        final long tmp = lastTime;
        handler.postAtTime(() -> Log.println(priority, tag, message), tmp);
    }
}
