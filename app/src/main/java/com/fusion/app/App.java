package com.fusion.app;

import com.shortvideo.lib.VideoApplication;

public class App extends VideoApplication {

    /**
     * 重写后，必须给此处2个赋值
     * RetrofitFactory.NEW_URL             为默认Api域名，防止Firebase因超时或获取失败导致没有Api域名
     * setProduct                          设置是否是生产环境，以此来判断读取Firebase哪个表的Api域名
     * 自定义属性
     * setVideoLayoutType                  选择视频页面布局，目前只有1,2， 1为右侧功能区，2为左侧功能区
     * setPureEnjoyment                    视频页面是否开启纯享功能，true则开启纯享模式，视频页面增加纯享功能按钮
     * setApplyDownload                    视频页面是否提供下载功能
     * setApplyToLike                      视频页面是否提供点赞功能
     * setOpenFrontPage                    开启前置页面，如果开启了，则setVideoLayoutType不生效，默认为1
     * setApplyFrontHomeMessage            前置页是否提供首页消息功能
     * setApplyFrontHomePhotos             前置页是否提供首页图库功能
     * setFrontListLayoutType              前置页面列表布局，1.两排格子  2.垂直布局
     * setFrontListItemHeight              前置页面2排格子列表单个的高度(单位dp)
     * setApplyFrontLikeNum                前置页面列表是否展示点赞数
     * setFrontLikeNumLayout               前置页面列表点赞数位置，1.左上  2.右下
     * setFrontPageBgColor                 设置前置页面背景色 (res id)
     * setFrontPageBottomBgColor           设置前置页底部菜单栏背景颜色 (res id)
     * setFrontPageTitleColor              设置前置页面标题、内容文字颜色 (res id)
     * setFrontPageTitleSize               设置前置页面标题文字大小 (单位sp)
     * setFrontPageIndicatorWidth          设置前置页面标题下划线宽度 (单位dp)
     * setFrontPageIndicatorHeight         设置前置页面标题下划线高度 (单位dp)
     * setFrontPageIndicatorColor          设置前置页面标题下划线颜色 (res id)
     * setFrontPageIndicatorCornersRadius  设置前置页面标题下划线圆角值 (单位float)
     * setFrontPageTakeVideo               设置前置页面是否提供拍摄功能
     */
    @Override
    protected void initVideoPageConfig() {
        setDefaultUrl("http://172.247.143.109:85/")               //默认域名
                .setProduct(false)                                //是否为生产环境
                .setVideoLayoutType(1)                            //视频使用布局1
                .setPureEnjoyment(false)                          //开启纯享功能
                .setApplyDownload(false)                          //提供下载功能
                .setApplyToLike(false)                            //提供点赞功能
                .setOpenFrontPage(true)                           //开启前置页面
                .setApplyFrontHomeMessage(true)                   //前置页是否提供首页消息功能
                .setApplyFrontHomePhotos(true)                    //前置页是否提供首页图库功能
                .setFrontListLayoutType(2)                        //前置页面列表布局，1.两排格子  2.垂直布局
                .setFrontListItemHeight(165)                      //前置页面2排格子列表单个的高度
                .setApplyFrontLikeNum(true)                       //前置页面列表是否展示点赞数
                .setFrontLikeNumLayout(1)                         //前置页面列表点赞数位置，1.左上  2.右下
                .setFrontPageBgColor(R.color.black)               //设置前置页背景颜色
                .setFrontPageBottomBgColor(R.color.color_181818)  //设置前置页底部菜单栏背景颜色
                .setFrontPageTitleColor(R.color.white)            //设置前置页面标题、内容文字颜色
                .setFrontPageTitleSize(20)                        //设置前置页面标题文字大小
                .setFrontPageIndicatorWidth(56)                   //设置前置页面标题下划线宽度
                .setFrontPageIndicatorHeight(6)                   //设置前置页面标题下划线高度
                .setFrontPageIndicatorColor(R.color.white)        //设置前置页面标题下划线颜色
                .setFrontPageIndicatorCornersRadius(16)           //设置前置页面标题下划线圆角值
                .setFrontPageTakeVideo(false);                    //设置前置页面是否提供拍摄功能
    }
}
