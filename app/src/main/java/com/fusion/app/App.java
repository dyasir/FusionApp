package com.fusion.app;

import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.http.RetrofitFactory;

public class App extends VideoApplication {

    /**
     * 重写后，必须给此处2个赋值
     * setProduct设置是否是生产环境，以此来判断读取Firebase哪个表的Api域名
     * RetrofitFactory.NEW_URL为默认Api域名，防止Firebase因超时或获取失败导致没有Api域名
     */
    @Override
    protected void initVideoApiUrl() {
//        super.initVideoApiUrl();
        setProduct(false);
        RetrofitFactory.NEW_URL = "http://172.247.143.109:85/";
    }
}
