package com.shortvideo.lib.ui.activity.front;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.gyf.immersionbar.ImmersionBar;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.databinding.TkActivityShortVideoFrontBinding;
import com.shortvideo.lib.model.HomeBean;
import com.shortvideo.lib.model.TabEntity;
import com.shortvideo.lib.ui.activity.front.message.TkFrontMessageFragment;
import com.shortvideo.lib.ui.activity.front.mine.TkFrontMineFragment;
import com.shortvideo.lib.ui.activity.front.videolist.TkFrontVideoFragment;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.SPUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TkShortVideoFrontActivity extends AppCompatActivity {

    TkActivityShortVideoFrontBinding binding;

    private Timer changeTimer;

    private final String[] tabText = new String[]{"", "", ""};
    //未选中icon
    private final int[] normalIcon = {R.mipmap.tk_icon_bottom_home, R.mipmap.tk_icon_bottom_message, R.mipmap.tk_icon_bottom_mine};
    //选中时icon
    private final int[] selectIcon = {R.mipmap.tk_icon_bottom_home_sel, R.mipmap.tk_icon_bottom_message_sel, R.mipmap.tk_icon_bottom_mine_sel};
    private final ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private final ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(VideoApplication.getInstance().getFrontPageBgColor())
                .navigationBarColor(VideoApplication.getInstance().getFrontPageBottomBgColor())
                .statusBarDarkFont(false, 0f)
                .init();

        binding = TkActivityShortVideoFrontBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
        initData();
    }

    private void initView() {
        HomeBean homeBeanPre = (HomeBean) getIntent().getSerializableExtra("homeBean");

        binding.bottom.setBackgroundResource(VideoApplication.getInstance().getFrontPageBottomBgColor());

        fragments.add(TkFrontVideoFragment.newInstants(homeBeanPre));
        fragments.add(new TkFrontMessageFragment());
        fragments.add(new TkFrontMineFragment());

        for (int i = 0; i < tabText.length; i++) {
            mTabEntities.add(new TabEntity(tabText[i], selectIcon[i], normalIcon[i]));
        }
        binding.bottom.setTabData(mTabEntities, this, R.id.fragment, fragments);
        binding.bottom.getTitleView(0).setVisibility(View.GONE);
        binding.bottom.getTitleView(1).setVisibility(View.GONE);
        binding.bottom.getTitleView(2).setVisibility(View.GONE);
    }

    private void initData() {
        if (VideoApplication.getInstance().getMaxWatchTime() > 0) {
            changeTimer = new Timer();
            changeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(3);
                }
            }, VideoApplication.getInstance().getMaxWatchTime());
        }
    }

    private final Handler handler = new Handler(msg -> {
        /** 到达总时长，跳转其他APP **/
        if (msg.what == 3) {
            SPUtils.set("fusion_jump", "1");
            HttpRequest.stateChange(TkShortVideoFrontActivity.this, 3, new HttpCallBack<List<String>>() {
                @Override
                public void onSuccess(List<String> list, String msg) {
                    //释放所有视频资源
                    GSYVideoManager.releaseAllVideos();
                    ARouter.getInstance()
                            .build(VideoApplication.THIRD_ROUTE_PATH)
                            .navigation();
                    overridePendingTransition(0, 0);
                    //跳转后结束掉视频APP所有业务
                    ActivityManager.getAppInstance().finishAllActivity();
                }

                @Override
                public void onFail(int errorCode, String errorMsg) {
                    //释放所有视频资源
                    GSYVideoManager.releaseAllVideos();
                    ARouter.getInstance()
                            .build(VideoApplication.THIRD_ROUTE_PATH)
                            .navigation();
                    overridePendingTransition(0, 0);
                    //跳转后结束掉视频APP所有业务
                    ActivityManager.getAppInstance().finishAllActivity();
                }
            });
        }
        return false;
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (changeTimer != null)
            changeTimer.cancel();
    }
}