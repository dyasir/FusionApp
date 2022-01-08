package com.shortvideo.lib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shortvideo.lib.R;

public class ToastyUtils {

    private static Toast toast;
    @SuppressLint("StaticFieldLeak")
    private static TextView str;

    public static void init(Context mContext) {
        if (toast == null)
            toast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        View view = View.inflate(mContext, R.layout.layout_toast, null);
        str = view.findViewById(R.id.message);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
    }

    public static void ToastShow(String message) {
        if (toast != null) {
            str.setText(message);
            toast.show();
        }
    }
}
