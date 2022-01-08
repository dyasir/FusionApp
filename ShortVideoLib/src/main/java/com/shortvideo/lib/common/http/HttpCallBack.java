package com.shortvideo.lib.common.http;

public abstract class HttpCallBack<T> {
    public abstract void onSuccess(T t, String msg);

    public abstract void onFail(int errorCode, String errorMsg);
}
