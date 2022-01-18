package com.shortvideo.lib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneInfoUtils {

    private final TelephonyManager telephonyManager;

    public PhoneInfoUtils(Context context) {
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    //获取电话号码
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public String getNativePhoneNumber() {
        String nativePhoneNumber = "N/A";
        nativePhoneNumber = telephonyManager.getLine1Number();
        return nativePhoneNumber;
    }

}