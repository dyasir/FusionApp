package com.shortvideo.lib.ui.widgets;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.TkAppConfig;
import com.shortvideo.lib.common.event.OnWaterEmptyEvent;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.databinding.TkPopDownloadBinding;
import com.shortvideo.lib.model.VideoPathBean;
import com.shortvideo.lib.ui.activity.TkReportActivity;
import com.shortvideo.lib.ui.activity.TkSettingActivity;
import com.shortvideo.lib.utils.ToastyUtils;
import com.tbruyelle.rxpermissions3.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;
import razerdp.basepopup.BasePopupWindow;
import razerdp.util.animation.AnimationHelper;
import razerdp.util.animation.TranslationConfig;

public class DownloadPop extends BasePopupWindow implements View.OnClickListener {

    TkPopDownloadBinding binding;

    private final int id;
    private final int position;
    private boolean isDownload = false;
    private String orgVideoPath;
    private String outVideoPath;
    private MyRxFFmpegSubscriber myRxFFmpegSubscriber;
    private final SharePop sharePop;

    public DownloadPop(Context context, int id, int position, SharePop sharePop) {
        super(context);
        this.id = id;
        this.position = position;
        this.sharePop = sharePop;

        binding = TkPopDownloadBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        setOutSideDismiss(true);
        setPopupGravity(Gravity.BOTTOM);
    }

    @Override
    public void onViewCreated(@NonNull View contentView) {
        super.onViewCreated(contentView);

        orgVideoPath = TkAppConfig.VIDEO_PATH + "org_video" + id + ".mp4";
        outVideoPath = TkAppConfig.OUT_VIDEO_PATH + "out_video" + id + ".mp4";
        if (!new File(TkAppConfig.OUT_VIDEO_PATH).exists())
            new File(TkAppConfig.OUT_VIDEO_PATH).mkdir();

        if (new File(TkAppConfig.VIDEO_PATH).exists() && new File(outVideoPath).exists()) {
            isDownload = true;
            binding.txProgress.setBackgroundResource(R.drawable.tk_bg_ff5370_6dp);
            binding.txProgress.setText(getContext().getString(R.string.tk_download_pop_photo));
            binding.txProgress.setTextColor(getContext().getResources().getColor(R.color.white));
        } else {
            isDownload = false;
            binding.txProgress.setBackgroundResource(android.R.color.transparent);
            binding.txProgress.setText(getContext().getString(R.string.tk_download_pop_start));
            binding.txProgress.setTextColor(getContext().getResources().getColor(R.color.color_555555));
        }

        binding.report.setOnClickListener(this);
        binding.download.setOnClickListener(this);
        binding.txProgress.setOnClickListener(this);
        binding.share.setOnClickListener(this);
        binding.set.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.report) {
            dismiss();
            intent = new Intent(getContext(), TkReportActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("position", position);
            getContext().startActivity(intent);
        } else if (v.getId() == R.id.download) {
            binding.report.setVisibility(View.GONE);
            binding.download.setVisibility(View.GONE);
            binding.share.setVisibility(View.GONE);
            binding.set.setVisibility(View.GONE);
            binding.rlProgress.setVisibility(View.VISIBLE);
            if (!isDownload) {
                RxPermissions rxPermissions = new RxPermissions((AppCompatActivity) getContext());
                rxPermissions
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                //若此时水印图为空，再次制作水印图
                                if (TextUtils.isEmpty(VideoApplication.getInstance().getWaterPicPath()))
                                    EventBus.getDefault().post(new OnWaterEmptyEvent());

                                HttpRequest.getDownLoadPath((AppCompatActivity) getContext(), id, new HttpCallBack<VideoPathBean>() {
                                    @Override
                                    public void onSuccess(VideoPathBean videoPathBean, String msg) {
                                        setOutSideDismiss(false);

                                        Aria.download(getContext())
                                                .load(videoPathBean.getDown_url())
                                                .setFilePath(orgVideoPath)
                                                .ignoreFilePathOccupy()
                                                .create();
                                    }

                                    @Override
                                    public void onFail(int errorCode, String errorMsg) {
                                        ToastyUtils.ToastShow(errorMsg);
                                    }
                                });
                            }
                        });
            }
        } else if (v.getId() == R.id.tx_progress) {
            if (isDownload) {
                dismiss();
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_GALLERY);
                getContext().startActivity(intent);
            }
        } else if (v.getId() == R.id.share) {
            dismiss();
            sharePop.showPopupWindow();
        } else if (v.getId() == R.id.set) {
            dismiss();
            intent = new Intent(getContext(), TkSettingActivity.class);
            intent.putExtra("id", id);
            getContext().startActivity(intent);
        }
    }

    /**
     * 打开下载
     */
    public void openDownload() {
        showPopupWindow();

        binding.report.setVisibility(View.GONE);
        binding.download.setVisibility(View.GONE);
        binding.share.setVisibility(View.GONE);
        binding.set.setVisibility(View.GONE);
        binding.rlProgress.setVisibility(View.VISIBLE);
        if (!isDownload) {
            RxPermissions rxPermissions = new RxPermissions((AppCompatActivity) getContext());
            rxPermissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            //若此时水印图为空，再次制作水印图
                            if (TextUtils.isEmpty(VideoApplication.getInstance().getWaterPicPath()))
                                EventBus.getDefault().post(new OnWaterEmptyEvent());

                            HttpRequest.getDownLoadPath((AppCompatActivity) getContext(), id, new HttpCallBack<VideoPathBean>() {
                                @Override
                                public void onSuccess(VideoPathBean videoPathBean, String msg) {
                                    setOutSideDismiss(false);

                                    Aria.download(getContext())
                                            .load(videoPathBean.getDown_url())
                                            .setFilePath(orgVideoPath)
                                            .ignoreFilePathOccupy()
                                            .create();
                                }

                                @Override
                                public void onFail(int errorCode, String errorMsg) {
                                    ToastyUtils.ToastShow(errorMsg);
                                }
                            });
                        }
                    });
        }
    }

    public void downloadComplete(DownloadTask task) {
        Log.e("result", "下载完成: " + task.getDownloadEntity().getPercent());
        setOutSideDismiss(false);
        binding.progress.setProgressCompat(100, true);

        binding.txProgress.postDelayed(() -> {
            binding.txProgress.setText(getContext().getString(R.string.tk_download_handle_ing));
            binding.progress.setProgressCompat(0, true);
            Log.e("result", "下载完成: ");
            String commen = "ffmpeg -y -i " + orgVideoPath + " -i " + VideoApplication.getInstance().getWaterPicPath() +
                    " -filter_complex overlay=main_w-overlay_w-10:main_h-overlay_h-10 -preset superfast " + outVideoPath;
            String[] commands = commen.split(" ");
            myRxFFmpegSubscriber = new MyRxFFmpegSubscriber();
            //开始执行FFmpeg命令
            RxFFmpegInvoke.getInstance()
                    .runCommandRxJava(commands)
                    .subscribe(myRxFFmpegSubscriber);
        }, 750);
    }

    public void downloadFail(DownloadTask task) {
        ToastyUtils.ToastShow(getContext().getString(R.string.tk_download_pop_faild));
        setOutSideDismiss(true);

        binding.report.setVisibility(View.VISIBLE);
        binding.download.setVisibility(View.VISIBLE);
        binding.share.setVisibility(View.VISIBLE);
        binding.set.setVisibility(View.VISIBLE);
        binding.rlProgress.setVisibility(View.GONE);
        isDownload = false;
        binding.txProgress.setBackgroundResource(android.R.color.transparent);
        binding.txProgress.setText(getContext().getString(R.string.tk_download_pop_start));
        binding.txProgress.setTextColor(getContext().getResources().getColor(R.color.color_555555));
    }

    public void downloadRunning(DownloadTask task) {
        binding.progress.setProgressCompat(task.getDownloadEntity().getPercent(), true);
        binding.txProgress.setText(getContext().getString(R.string.tk_download_pop_ing, task.getDownloadEntity().getConvertFileSize(),
                task.getDownloadEntity().getPercent()));
        Log.e("result", "下载进度: " + task.getDownloadEntity().getPercent());
    }

    /**
     * 添加水印
     */
    private class MyRxFFmpegSubscriber extends RxFFmpegSubscriber {

        public MyRxFFmpegSubscriber() {
            Log.e("result", "视频原地址: " + orgVideoPath + ", 视频输出地址: " + outVideoPath);
        }

        @Override
        public void onFinish() {
            Log.e("result", "水印结束: ");
            setOutSideDismiss(true);
            binding.progress.setProgressCompat(100, true);
            //把图片保存后声明这个广播事件通知系统相册有新图片到来
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(outVideoPath));
            intent.setData(uri);
            getContext().sendBroadcast(intent);

            //删除原视频
            new File(orgVideoPath).delete();

            binding.txProgress.postDelayed(() -> {
                isDownload = true;
                binding.txProgress.setBackgroundResource(R.drawable.tk_bg_ff5370_6dp);
                binding.txProgress.setText(getContext().getString(R.string.tk_download_pop_photo));
                binding.txProgress.setTextColor(getContext().getResources().getColor(R.color.white));
            }, 750);
        }

        @Override
        public void onProgress(int progress, long progressTime) {
            Log.e("result", "水印中: " + progress + ", " + progressTime);
            if (progress < 0) {
                progress = 0;
            }
            binding.progress.setProgressCompat(progress, true);
            binding.txProgress.setText(getContext().getString(R.string.tk_download_pop_handle, progress));
        }

        @Override
        public void onCancel() {
            Log.e("result", "水印取消: ");
        }

        @Override
        public void onError(String message) {
            Log.e("result", "水印错误: " + message);
            ToastyUtils.ToastShow(getContext().getString(R.string.tk_download_pop_handle_error));
            setOutSideDismiss(true);

            binding.report.setVisibility(View.VISIBLE);
            binding.download.setVisibility(View.VISIBLE);
            binding.share.setVisibility(View.VISIBLE);
            binding.set.setVisibility(View.VISIBLE);
            binding.rlProgress.setVisibility(View.GONE);
            isDownload = false;
            binding.txProgress.setBackgroundResource(android.R.color.transparent);
            binding.txProgress.setText(getContext().getString(R.string.tk_download_pop_start));
            binding.txProgress.setTextColor(getContext().getResources().getColor(R.color.color_555555));
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (myRxFFmpegSubscriber != null)
            myRxFFmpegSubscriber.dispose();
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return AnimationHelper.asAnimation()
                .withTranslation(TranslationConfig.FROM_BOTTOM)
                .toShow();
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return AnimationHelper.asAnimation()
                .withTranslation(TranslationConfig.TO_BOTTOM)
                .toShow();
    }
}
