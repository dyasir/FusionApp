package com.fusion.switchlib.http;

public abstract class HttpCallBack<T> {
    public abstract void onSuccess(T t, String msg);

    public abstract void onSuccess(String country, String countryCode, String query);

    public abstract void onFail(int errorCode, String errorMsg);
}
