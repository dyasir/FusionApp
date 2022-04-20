package com.fusion.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.fusion.app.databinding.ActivityEmptyBinding;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.shortvideo.lib.SwitchBaseActivity;
import com.shortvideo.lib.SwitchJumpListener;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.model.ConfigBean;
import com.shortvideo.lib.model.DataMgr;
import com.shortvideo.lib.ui.activity.TkVideoSplashActivity;
import com.shortvideo.lib.utils.SPUtils;

import java.util.List;

public class EmptyActivity extends SwitchBaseActivity implements SwitchJumpListener {

    ActivityEmptyBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSwitchJumpListener(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .navigationBarColor(R.color.white)
                .statusBarDarkFont(false, 0f)
                .init();

        binding = ActivityEmptyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void jumpPackageA() {
        startActivity(new Intent(this, TkVideoSplashActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    public void jumpPackageB() {
        startActivity(new Intent(this, GameBActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    /**
     * 跳转B包上报
     */
    private void reportFusionB() {
        if (TextUtils.isEmpty(SPUtils.getString("fusion_report")))
            if (TextUtils.isEmpty(SPUtils.getString("fusion_get_configs"))) {
                HttpRequest.getConfigs(this, VideoApplication.getInstance().getUtm_source(), VideoApplication.getInstance().getUtm_medium(),
                        VideoApplication.getInstance().getUtm_install_time(), VideoApplication.getInstance().getUtm_version(), new HttpCallBack<ConfigBean>() {
                            @Override
                            public void onSuccess(ConfigBean configBean, String msg) {
                                Log.e("result", "获取配置成功");
                                configBean.setSysInfo(VideoApplication.getInstance().getSysInfo());
                                configBean.setUdid(VideoApplication.getInstance().getUDID());
                                configBean.setAppVersion(VideoApplication.getInstance().getVerName());
                                DataMgr.getInstance().setUser(configBean);

                                //记录是否已经获取配置
                                SPUtils.set("fusion_get_configs", new Gson().toJson(configBean));

                                HttpRequest.stateChange(EmptyActivity.this, 5, new HttpCallBack<List<String>>() {
                                    @Override
                                    public void onSuccess(List<String> strings, String msg) {
                                        SPUtils.set("fusion_report", "1");
                                    }

                                    @Override
                                    public void onFail(int errorCode, String errorMsg) {
                                        reportFusionB();
                                    }
                                });
                            }

                            @Override
                            public void onFail(int errorCode, String errorMsg) {
                                Log.e("result", "获取配置失败");
                                reportFusionB();
                            }
                        });
            } else {
                HttpRequest.stateChange(EmptyActivity.this, 5, new HttpCallBack<List<String>>() {
                    @Override
                    public void onSuccess(List<String> strings, String msg) {
                        SPUtils.set("fusion_report", "1");
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        reportFusionB();
                    }
                });
            }
    }
}
