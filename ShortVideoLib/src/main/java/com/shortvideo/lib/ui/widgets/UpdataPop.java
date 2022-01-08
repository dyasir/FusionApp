package com.shortvideo.lib.ui.widgets;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.shortvideo.lib.R;
import com.shortvideo.lib.common.AppConfig;
import com.shortvideo.lib.databinding.PopUpdataBinding;
import com.shortvideo.lib.ui.callback.OnUpdataCallback;
import com.shortvideo.lib.utils.ToastyUtils;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.File;

import razerdp.basepopup.BasePopupWindow;

public class UpdataPop extends BasePopupWindow implements View.OnClickListener {

    PopUpdataBinding binding;

    private final boolean isCompulsory;
    private final String url;
    private final OnUpdataCallback onUpdataCallback;
    private boolean isFinish = false;
    private String downPath;

    public UpdataPop(Context context, boolean isCompulsory, String url, OnUpdataCallback onUpdataCallback) {
        super(context);
        this.isCompulsory = isCompulsory;
        this.url = url;
        this.onUpdataCallback = onUpdataCallback;

        binding = PopUpdataBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        setOutSideDismiss(false);
        setBackPressEnable(false);
        setPopupGravity(Gravity.CENTER);
    }

    @Override
    public void onViewCreated(@NonNull View contentView) {
        super.onViewCreated(contentView);

        binding.tip.setVisibility(isCompulsory ? View.VISIBLE : View.GONE);
        binding.btn.setVisibility(isCompulsory ? View.VISIBLE : View.GONE);
        binding.llBtn.setVisibility(isCompulsory ? View.GONE : View.VISIBLE);

        binding.btn.setOnClickListener(this);
        binding.btn2.setOnClickListener(this);
        binding.btn3.setOnClickListener(this);
        binding.rlProgress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        RxPermissions rxPermissions;
        if (v.getId() == R.id.btn){
            if (TextUtils.isEmpty(url)) {
                ToastyUtils.ToastShow("Không có liên kết tải xuống");
            } else {
                rxPermissions = new RxPermissions((AppCompatActivity)getContext());
                rxPermissions
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                binding.rlProgress.setVisibility(View.VISIBLE);
                                binding.btn.setVisibility(View.GONE);

                                Aria.download(getContext())
                                        .load(url)
                                        .setFilePath(AppConfig.FILE_PATH + url.substring(Math.max(url.lastIndexOf("/") + 1, 0)))
                                        .ignoreFilePathOccupy()
                                        .create();
                            }
                        });
            }
        }else if (v.getId() == R.id.btn2){
            if (TextUtils.isEmpty(url)) {
                ToastyUtils.ToastShow("Không có liên kết tải xuống");
            } else {
                rxPermissions = new RxPermissions((AppCompatActivity) getContext());
                rxPermissions
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                binding.rlProgress.setVisibility(View.VISIBLE);
                                binding.btn.setVisibility(View.GONE);

                                Aria.download(getContext())
                                        .load(url)
                                        .setFilePath(AppConfig.FILE_PATH + url.substring(Math.max(url.lastIndexOf("/") + 1, 0)))
                                        .ignoreFilePathOccupy()
                                        .create();
                            }
                        });
            }
        }else if (v.getId() == R.id.btn3){
            dismiss();
            if (onUpdataCallback != null)
                onUpdataCallback.onUpdataLater();
        } else if (v.getId() == R.id.rl_progress){
            if (isFinish)
                installAPK(downPath);
        }
    }

    public void downloadComplete(DownloadTask task) {
        Log.e("result", "下载完成: " + task.getDownloadEntity().getPercent());
        binding.progress.setProgressCompat(100, true);
        binding.txProgress.setBackgroundResource(R.drawable.bg_ff5370_6dp);
        binding.txProgress.setText("Bấm cài đặt");
        binding.txProgress.setTextColor(getContext().getResources().getColor(R.color.white));
        isFinish = true;
        downPath = task.getFilePath();

        binding.txProgress.postDelayed(() -> {
            Log.e("result", "下载完成: ");
            installAPK(downPath);
        }, 500);
    }

    public void downloadFail(DownloadTask task) {
        if (isCompulsory) {
            binding.btn.setVisibility(View.VISIBLE);
        } else {
            binding.llBtn.setVisibility(View.VISIBLE);
        }
        binding.rlProgress.setVisibility(View.GONE);
        binding.txProgress.setText("Bắt đầu Tải về...");
        ToastyUtils.ToastShow("Tải xuống không thành công, vui lòng thử lại!");
    }

    public void downloadRunning(DownloadTask task) {
        binding.progress.setProgressCompat(task.getDownloadEntity().getPercent(), true);
        binding.txProgress.setText("tải xuống " + task.getDownloadEntity().getConvertFileSize() + " / " + task.getDownloadEntity().getPercent() + "%");
        Log.e("result", "下载进度: " + task.getDownloadEntity().getPercent());
    }

    /**
     * 安装apk
     *
     * @param path
     */
    private void installAPK(String path) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            if (!getContext().getPackageManager().canRequestPackageInstalls())//没有权限
//                ToastyUtils.ToastShow("cài đặt thất bại");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        } else {
            Uri apkUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", new File(path));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }
        getContext().startActivity(intent);
    }
}
