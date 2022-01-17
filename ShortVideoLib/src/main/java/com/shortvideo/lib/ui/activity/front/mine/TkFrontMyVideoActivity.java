package com.shortvideo.lib.ui.activity.front.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.logger.Logger;
import com.shortvideo.lib.R;
import com.shortvideo.lib.common.TkAppConfig;
import com.shortvideo.lib.databinding.TkActivityFrontMyVideoBinding;
import com.shortvideo.lib.model.SelfieBean;
import com.shortvideo.lib.utils.ActivityManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class TkFrontMyVideoActivity extends AppCompatActivity {

    TkActivityFrontMyVideoBinding binding;

    private TkFrontMyVideoAdapter tkFrontMyVideoAdapter;
    private MyRxFFmpegSubscriber myRxFFmpegSubscriber;
    private final List<SelfieBean> videoPath = new ArrayList<>();
    private int handlePosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .navigationBarColor(R.color.white)
                .statusBarDarkFont(true, 0f)
                .init();

        binding = TkActivityFrontMyVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        tkFrontMyVideoAdapter = new TkFrontMyVideoAdapter(new ArrayList<>());
        binding.recycle.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recycle.setAdapter(tkFrontMyVideoAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData() {
        File file = new File(TkAppConfig.MY_VIDEO_PATH);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile() && f.getName().endsWith(".mp4")) {
                        SelfieBean selfieBean = new SelfieBean(f.getPath(), "");
                        videoPath.add(selfieBean);
                        for (File fs : files) {
                            if (fs.isFile() && fs.getName().endsWith(".jpg") && fs.getName().substring(0, fs.getName().lastIndexOf("."))
                                    .equals(f.getName().substring(0, f.getName().lastIndexOf(".")))) {
                                selfieBean.setVideoThum(fs.getPath());
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (videoPath.size() > 0) {
            for (int i = 0; i < videoPath.size(); i++) {
                if (TextUtils.isEmpty(videoPath.get(i).getVideoThum())) {
                    handlePosition = i;
                    String name = new File(videoPath.get(handlePosition).getVideoPath()).getName().replace(".mp4", ".jpg");
                    String commen = "ffmpeg -y -i " + videoPath.get(handlePosition).getVideoPath() + " -f image2 -ss 00:00:01 -vframes 1 -preset superfast " +
                            TkAppConfig.MY_VIDEO_PATH + name;
                    String[] commands = commen.split(" ");
                    myRxFFmpegSubscriber = new MyRxFFmpegSubscriber();
                    //开始执行FFmpeg命令
                    RxFFmpegInvoke.getInstance()
                            .runCommandRxJava(commands)
                            .subscribe(myRxFFmpegSubscriber);
                    break;
                }
            }
        }
        tkFrontMyVideoAdapter.setList(videoPath);
    }

    private void initListener() {
        binding.back.setOnClickListener(view -> {
            finish();
        });

        tkFrontMyVideoAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(TkFrontMyVideoActivity.this, TkFrontMyVideoDetailActivity.class);
            intent.putExtra("url", videoPath.get(position).getVideoPath());
            startActivity(intent);
        });
    }

    /**
     * 获取视频第一帧
     */
    private class MyRxFFmpegSubscriber extends RxFFmpegSubscriber {

        public MyRxFFmpegSubscriber() {
            Logger.e(videoPath.get(handlePosition).getVideoPath() + ", " +
                    TkAppConfig.MY_VIDEO_PATH + new File(videoPath.get(handlePosition).getVideoPath()).getName().replace(".mp4", ".jpg"));
        }

        @Override
        public void onFinish() {
            Log.e("result", "视频第一帧结束: " + new File(videoPath.get(handlePosition).getVideoPath()).getName().replace(".mp4", ".jpg"));
            videoPath.get(handlePosition).setVideoThum(TkAppConfig.MY_VIDEO_PATH +
                    new File(videoPath.get(handlePosition).getVideoPath()).getName().replace(".mp4", ".jpg"));
            tkFrontMyVideoAdapter.notifyItemChanged(handlePosition);

            for (int i = handlePosition + 1; i < videoPath.size(); i++) {
                if (TextUtils.isEmpty(videoPath.get(i).getVideoThum())) {
                    handlePosition = i;
                    String name = new File(videoPath.get(handlePosition).getVideoPath()).getName().replace(".mp4", ".jpg");
                    String commen = "ffmpeg -y -i " + videoPath.get(handlePosition).getVideoPath() + " -f image2 -ss 00:00:01 -vframes 1 -preset superfast " +
                            TkAppConfig.MY_VIDEO_PATH + name;
                    String[] commands = commen.split(" ");
                    myRxFFmpegSubscriber = new MyRxFFmpegSubscriber();
                    //开始执行FFmpeg命令
                    RxFFmpegInvoke.getInstance()
                            .runCommandRxJava(commands)
                            .subscribe(myRxFFmpegSubscriber);
                    break;
                }
            }
        }

        @Override
        public void onProgress(int progress, long progressTime) {
            Log.e("result", "视频第一帧: " + progress);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(String message) {
            Log.e("result", "视频第一帧错误: " + message);
            for (int i = handlePosition + 1; i < videoPath.size(); i++) {
                if (TextUtils.isEmpty(videoPath.get(i).getVideoThum())) {
                    handlePosition = i;
                    String name = new File(videoPath.get(handlePosition).getVideoPath()).getName().replace(".mp4", ".jpg");
                    String commen = "ffmpeg -y -i " + videoPath.get(handlePosition).getVideoPath() + " -f image2 -ss 00:00:01 -vframes 1 -preset superfast " +
                            TkAppConfig.MY_VIDEO_PATH + name;
                    String[] commands = commen.split(" ");
                    myRxFFmpegSubscriber = new MyRxFFmpegSubscriber();
                    //开始执行FFmpeg命令
                    RxFFmpegInvoke.getInstance()
                            .runCommandRxJava(commands)
                            .subscribe(myRxFFmpegSubscriber);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myRxFFmpegSubscriber != null)
            myRxFFmpegSubscriber.dispose();
    }
}
