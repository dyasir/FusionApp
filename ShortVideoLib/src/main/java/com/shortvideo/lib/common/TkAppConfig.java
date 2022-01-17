package com.shortvideo.lib.common;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.utils.TimeDateUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TkAppConfig {

    //图片缓存地址
    public static final String IMAGE_SHUI_PATH = VideoApplication.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator;
    //视频缓存地址
    public static final String VIDEO_PATH = VideoApplication.getInstance().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator;
    //自拍视频存放地址
    public static final String MY_VIDEO_PATH = VideoApplication.getInstance().getExternalFilesDir(Environment.DIRECTORY_MOVIES) + File.separator;
    //添加水印后的视频地址
    public static final String OUT_VIDEO_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Camera" + File.separator;
    //下载APK地址
    public static final String FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator;

    //bugly id
    public static final String BUGLY_ID = "950757ae7c";

    /**
     * 获取水印图
     *
     * @return
     */
    public static String getWaterPath(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e("result", "水印bitmap为空");
            return "";
        }

        File dirFile = new File(TkAppConfig.IMAGE_SHUI_PATH);
        if (!dirFile.exists())
            dirFile.mkdir();
        String fileName = TimeDateUtils.getCurTimeLong() + "water.png";
        File file = new File(TkAppConfig.IMAGE_SHUI_PATH + File.separator + fileName);
        if (!file.exists()) {
            OutputStream os;
            try {
                file.createNewFile();
                os = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
                if (!bitmap.isRecycled())
                    bitmap.recycle();

                Log.e("result", "水印图保存: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file.getAbsolutePath();
    }
}
