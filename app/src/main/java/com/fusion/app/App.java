package com.fusion.app;

import com.shortvideo.lib.VideoApplication;

public class App extends VideoApplication {

    @Override
    protected void initVideoPageConfig() {
        setDefaultUrl("http://172.247.143.109:85/")               //默认域名
                .setProduct(false)                                //是否为生产环境
                .setApiVersion("1.0.0.1")                         //是否加密解密
                .setVideoLayoutType(1)                            //选择加载视频布局1(右侧按钮栏)，或布局2(左侧按钮栏)
                .setPureEnjoyment(true)                           //开启纯享功能
                .setApplyDownload(true)                           //提供下载功能
                .setApplyToLike(true)                             //提供点赞功能
                .setOpenPageWhere(3);                              //视频App启动页面，1.新版首页  2.前置页  3.旧版短视频
    }
}
