package com.shortvideo.lib.ui.activity.concise.photoedit;

import android.content.Context;

public class IMG {

    private static Context mApplicationContext;

    public static void initialize(Context context) {
        mApplicationContext = context.getApplicationContext();

    }

    public static class Config {

        private boolean isSave;

    }
}
