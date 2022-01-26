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
     * setOpenPageWhere                    视频App启动页面，1.新版首页  2.前置页  3.旧版短视频
     * setApplyFrontHomeVideo              前置页是否提供首页视频功能
     * setApplyFrontHomeMessage            前置页是否提供首页消息功能
     * setApplyFrontHomePhotos             前置页是否提供首页图库功能
     * setApplyFrontHomeEdit               前置页是否提供首页图片编辑功能
     * setApplyFrontHomeMine               前置页是否提供首页我的功能
     * setFrontListLayoutType              前置页列表布局，1.两排格子  2.垂直布局
     * setFrontListItemHeight              前置页2排格子列表单个的高度(单位dp)
     * setApplyFrontLikeNum                前置页列表是否展示点赞数
     * setFrontLikeNumLayout               前置页列表点赞数位置，1.左上  2.右下
     * setFrontPageBgColor                 前置页背景色 (res id)
     * setNcIndexBgColor                   新版首页背景颜色
     * setNcIndexTitleColor                新版首页标题文字颜色
     * setFrontPageBottomBgColor           前置页底部菜单栏背景颜色 (res id)
     * setApplyFrontPageTitle              前置页是否展示标题(如果不展示，则下划线也不展示)
     * setFrontPageTitleColor              前置页标题、内容文字颜色 (res id)(目前保持标题文字和内容文字颜色一致，防止与背景色互斥)
     * setFrontPageTitleSize               前置页标题文字大小 (单位sp)
     * setFrontPageTitleLayoutType         前置页标题相对位置，1.居左  2.居中  3.居右
     * setApplyFrontPageIndicator          前置页是否展示标题下划线
     * setFrontPageIndicatorLayoutType     前置页下划线相对标题位置，1.居左  2.居中  3.居右
     * setFrontPageIndicatorWidth          前置页标题下划线宽度 (单位dp)
     * setFrontPageIndicatorHeight         前置页标题下划线高度 (单位dp)
     * setFrontPageIndicatorColor          前置页标题下划线颜色 (res id)
     * setFrontPageIndicatorCornersRadius  前置页标题下划线圆角值 (单位float)
     * setApplyFrontPageTakeVideo          前置页是否提供拍摄功能
     * setFrontPhotosLayoutType            前置页图库列表样式，1.列表  2.一屏多页
     * setFrontPhotosScollType             前置页图库列表一屏多页滑动方式，1.左右  2.上下
     * setFrontPhotosSpanCount             前置页图库列表的列数(2-4列)
     * setApplyFrontPhotosLikeNum          前置页图库是否展示点赞数
     * setApplyFrontPhotosWallpaper        前置页图库是否提供设置壁纸功能
     */
    @Override
    protected void initVideoPageConfig() {
        setDefaultUrl("http://172.247.143.109:85/")               //默认域名
                .setProduct(false)                                //是否为生产环境
                .setVideoLayoutType(1)                            //视频使用布局1
                .setPureEnjoyment(false)                          //开启纯享功能
                .setApplyDownload(false)                          //提供下载功能
                .setApplyToLike(false)                            //提供点赞功能
                .setOpenPageWhere(2)                              //视频App启动页面，1.新版首页  2.前置页  3.旧版短视频
                .setApplyFrontHomeVideo(true)                    //前置页是否提供首页视频功能
                .setApplyFrontHomeMessage(true)                  //前置页是否提供首页消息功能
                .setApplyFrontHomePhotos(true)                    //前置页是否提供首页图库功能
                .setApplyFrontHomeEdit(true)                      //前置页是否提供首页图片编辑功能
                .setApplyFrontHomeMine(true)                      //前置页是否提供首页我的功能
                .setFrontListLayoutType(2)                        //前置页列表布局，1.两排格子  2.垂直布局
                .setFrontListItemHeight(165)                      //前置页2排格子列表单个的高度
                .setApplyFrontLikeNum(true)                       //前置页列表是否展示点赞数
                .setFrontLikeNumLayout(1)                         //前置页列表点赞数位置，1.左上  2.右下
                .setFrontPageBgColor(R.color.black)               //前置页背景颜色
                .setNcIndexBgColor(R.color.white)                 //新版首页背景颜色
                .setNcIndexTitleColor(R.color.black)              //新版首页标题文字颜色
                .setFrontPageBottomBgColor(R.color.color_181818)  //前置页底部菜单栏背景颜色
                .setApplyFrontPageTitle(true)                     //前置页是否展示标题(如果不展示，则下划线也不展示)
                .setFrontPageTitleColor(R.color.white)            //前置页面标题、内容文字颜色
                .setFrontPageTitleSize(18)                        //前置页面标题文字大小
                .setFrontPageTitleLayoutType(1)                   //前置页标题相对位置，1.居左  2.居中  3.居右
                .setApplyFrontPageIndicator(true)                 //前置页是否展示标题下划线
                .setFrontPageIndicatorLayoutType(1)               //前置页下划线相对标题位置，1.居左  2.居中  3.居右
                .setFrontPageIndicatorWidth(13)                   //前置页标题下划线宽度
                .setFrontPageIndicatorHeight(3)                   //前置页标题下划线高度
                .setFrontPageIndicatorColor(R.color.white)        //前置页标题下划线颜色
                .setFrontPageIndicatorCornersRadius(3)            //前置页标题下划线圆角值
                .setApplyFrontPageTakeVideo(true)                //前置页是否提供拍摄功能
                .setFrontPhotosLayoutType(2)                      //前置页图库列表样式，1.列表  2.一屏多页
                .setFrontPhotosScollType(2)                       //前置页图库列表一屏多页滑动方式，1.左右  2.上下
                .setFrontPhotosSpanCount(3)                       //前置页图库列表的列数(2-4列)
                .setApplyFrontPhotosLikeNum(false)                //前置页图库是否展示点赞数
                .setApplyFrontPhotosWallpaper(false);             //前置页图库是否提供设置壁纸功能
    }
}
