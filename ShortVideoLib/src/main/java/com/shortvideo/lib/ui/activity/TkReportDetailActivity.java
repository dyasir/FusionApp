package com.shortvideo.lib.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.language.LanguageConfig;
import com.shortvideo.lib.R;
import com.shortvideo.lib.common.event.OnVideoReportEvent;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.databinding.TkActivityReportDetailBinding;
import com.shortvideo.lib.ui.adapter.ReportDetailAdapter;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.ClickUtil;
import com.shortvideo.lib.utils.MojitoShow;
import com.shortvideo.lib.utils.ToastyUtils;
import com.shortvideo.lib.utils.pictureselector.GlideEngine;
import com.tbruyelle.rxpermissions3.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TkReportDetailActivity extends AppCompatActivity {

    TkActivityReportDetailBinding binding;

    private ReportDetailAdapter reportDetailAdapter;
    private int selNum = 0;
    private int id;
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .navigationBarColor(R.color.white)
                .keyboardEnable(true)
                .statusBarDarkFont(true, 0f)
                .init();

        binding = TkActivityReportDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
        initListener();
        initData();
    }

    private void initView() {
        id = getIntent().getIntExtra("id", 0);
        position = getIntent().getIntExtra("position", 0);
        binding.name.setText(getIntent().getStringExtra("name"));

        reportDetailAdapter = new ReportDetailAdapter(new ArrayList<>());
        binding.recycle.setLayoutManager(new GridLayoutManager(this, 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        binding.recycle.setAdapter(reportDetailAdapter);
    }

    @SuppressLint("AutoDispose")
    private void initListener() {
        binding.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.number.setText(Objects.requireNonNull(binding.et.getText()).toString().length() + "/200");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        reportDetailAdapter.addChildClickViewIds(R.id.img, R.id.rl_close, R.id.ll_photo);
        reportDetailAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (ClickUtil.isFastClick()) return;

            if (view.getId() == R.id.img){
                MojitoShow.recyclerView(this, binding.recycle, R.id.img, reportDetailAdapter.getData(), position);
            }else if (view.getId() == R.id.rl_close){
                reportDetailAdapter.removeAt(position);
                selNum--;
                if (reportDetailAdapter.getData().size() > 0) {
                    if (!TextUtils.isEmpty(reportDetailAdapter.getData().get(reportDetailAdapter.getData().size() - 1)))
                        reportDetailAdapter.addData("");
                } else {
                    reportDetailAdapter.addData("");
                }
            }
            else if (view.getId() == R.id.ll_photo){
                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                PictureSelector.create(TkReportDetailActivity.this)
                                        .openGallery(PictureMimeType.ofImage())
                                        .imageEngine(GlideEngine.createGlideEngine())
                                        .maxSelectNum(4 - selNum)
                                        .imageSpanCount(4)
                                        .isReturnEmpty(false)
                                        .isNotPreviewDownload(false)
                                        .isSingleDirectReturn(false)
                                        .selectionMode(PictureConfig.MULTIPLE)
                                        .isPreviewImage(true)
                                        .isCamera(false)
                                        .isZoomAnim(false)
                                        .isEnableCrop(false)
                                        .setCircleStrokeWidth(0)
                                        .isCompress(true)
                                        .isGif(false)
                                        .isPreviewEggs(true)
                                        .isOpenClickSound(false)
                                        .synOrAsy(true)
                                        .forResult(PictureConfig.CHOOSE_REQUEST);
                            }
                        });
            }
        });

        binding.btn.setOnClickListener(v -> {
            HttpRequest.report(this, Objects.requireNonNull(binding.et.getText()).toString(), new HttpCallBack<List<String>>() {
                @Override
                public void onSuccess(List<String> s, String msg) {
                    ToastyUtils.ToastShow(getString(R.string.tk_report_detail_commit_success));

                    EventBus.getDefault().post(new OnVideoReportEvent(id, position));
                    ActivityManager.getAppInstance().finishActivity(TkReportActivity.class);
                    finish();
                }

                @Override
                public void onFail(int errorCode, String errorMsg) {
                    ToastyUtils.ToastShow(errorMsg);
                }
            });
        });
    }

    private void initData() {
        reportDetailAdapter.addData("");
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == RESULT_OK) {
            List<LocalMedia> result = PictureSelector.obtainMultipleResult(data);
            int j = 0;
            for (int i = 0; i < reportDetailAdapter.getData().size(); i++) {
                if (TextUtils.isEmpty(reportDetailAdapter.getData().get(i))) {
                    j = i;
                    break;
                }
            }
            List<String> list = new ArrayList<>();
            for (LocalMedia localMedia : result) {
                list.add(localMedia.getCompressPath());
            }
            reportDetailAdapter.removeAt(j);
            reportDetailAdapter.addData(list);
            selNum = reportDetailAdapter.getData().size();
            if (reportDetailAdapter.getData().size() < 4)
                reportDetailAdapter.addData("");
            reportDetailAdapter.notifyDataSetChanged();
        }
    }
}
