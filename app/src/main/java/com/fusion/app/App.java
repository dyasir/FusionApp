package com.fusion.app;

import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.http.RetrofitFactory;

public class App extends VideoApplication {

    /**
     * 重写后，必须给此处2个赋值
     * setProduct设置是否是生产环境，以此来判断读取Firebase哪个表的Api域名
     * RetrofitFactory.NEW_URL为默认Api域名，防止Firebase因超时或获取失败导致没有Api域名
     * setVideoLayoutType选择视频页面布局，目前只有1,2， 1为右侧功能区，2为左侧功能区
     * setPureEnjoyment是否开启纯享功能，true则开启纯享模式，视频页面增加纯享功能按钮
     */
    @Override
    protected void initVideoApiUrl() {
//        super.initVideoApiUrl();
        //是否为生产环境
        setProduct(false);
        //默认域名
        RetrofitFactory.NEW_URL = "http://172.247.143.109:85/";
        //视频使用布局1
        setVideoLayoutType(2);
        //开启纯享功能
        setPureEnjoyment(true);
    }
}
