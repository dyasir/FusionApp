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
import com.shortvideo.lib.ui.activity.front.photos.TkFrontPhotosFragment;
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

    private String[] tabText;
    //未选中icon
    private int[] normalIcon;
    //选中时icon
    private int[] selectIcon;
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

        if (VideoApplication.getInstance().isApplyFrontHomeVideo()) {
            fragments.add(TkFrontVideoFragment.newInstants(homeBeanPre));
            if (VideoApplication.getInstance().isApplyFrontHomeMessage()) {
                fragments.add(new TkFrontMessageFragment());
                if (VideoApplication.getInstance().isApplyFrontHomePhotos()) {
                    fragments.add(new TkFrontPhotosFragment());
                    if (VideoApplication.getInstance().isApplyFrontHomeMine()) {
                        fragments.add(new TkFrontMineFragment());
                        tabText = new String[]{"", "", "", ""};
                        normalIcon = new int[]{R.mipmap.tk_icon_bottom_home, R.mipmap.tk_icon_bottom_message, R.mipmap.tk_icon_bottom_photos, R.mipmap.tk_icon_bottom_mine};
                        selectIcon = new int[]{R.mipmap.tk_icon_bottom_home_sel, R.mipmap.tk_icon_bottom_message_sel, R.mipmap.tk_icon_bottom_photos_sel, R.mipmap.tk_icon_bottom_mine_sel};
                    } else {
                        tabText = new String[]{"", "", ""};
                        normalIcon = new int[]{R.mipmap.tk_icon_bottom_home, R.mipmap.tk_icon_bottom_message, R.mipmap.tk_icon_bottom_photos};
                        selectIcon = new int[]{R.mipmap.tk_icon_bottom_home_sel, R.mipmap.tk_icon_bottom_message_sel, R.mipmap.tk_icon_bottom_photos_sel};
                    }
                } else {
                    if (VideoApplication.getInstance().isApplyFrontHomeMine()) {
                        fragments.add(new TkFrontMineFragment());
                        tabText = new String[]{"", "", ""};
                        normalIcon = new int[]{R.mipmap.tk_icon_bottom_home, R.mipmap.tk_icon_bottom_message, R.mipmap.tk_icon_bottom_mine};
                        selectIcon = new int[]{R.mipmap.tk_icon_bottom_home_sel, R.mipmap.tk_icon_bottom_message_sel, R.mipmap.tk_icon_bottom_mine_sel};
                    } else {
                        tabText = new String[]{"", ""};
                        normalIcon = new int[]{R.mipmap.tk_icon_bottom_home, R.mipmap.tk_icon_bottom_message};
                        selectIcon = new int[]{R.mipmap.tk_icon_bottom_home_sel, R.mipmap.tk_icon_bottom_message_sel};
                    }
                }
            } else {
                if (VideoApplication.getInstance().isApplyFrontHomePhotos()) {
                    fragments.add(new TkFrontPhotosFragment());
                    if (VideoApplication.getInstance().isApplyFrontHomeMine()) {
                        fragments.add(new TkFrontMineFragment());
                        tabText = new String[]{"", "", ""};
                        normalIcon = new int[]{R.mipmap.tk_icon_bottom_home, R.mipmap.tk_icon_bottom_photos, R.mipmap.tk_icon_bottom_mine};
                        selectIcon = new int[]{R.mipmap.tk_icon_bottom_home_sel, R.mipmap.tk_icon_bottom_photos_sel, R.mipmap.tk_icon_bottom_mine_sel};
                    } else {
                        tabText = new String[]{"", ""};
                        normalIcon = new int[]{R.mipmap.tk_icon_bottom_home, R.mipmap.tk_icon_bottom_photos};
                        selectIcon = new int[]{R.mipmap.tk_icon_bottom_home_sel, R.mipmap.tk_icon_bottom_photos_sel};
                    }
                } else {
                    if (VideoApplication.getInstance().isApplyFrontHomeMine()) {
                        fragments.add(new TkFrontMineFragment());
                        tabText = new String[]{"", ""};
                        normalIcon = new int[]{R.mipmap.tk_icon_bottom_home, R.mipmap.tk_icon_bottom_mine};
                        selectIcon = new int[]{R.mipmap.tk_icon_bottom_home_sel, R.mipmap.tk_icon_bottom_mine_sel};
                    } else {
                        tabText = new String[]{""};
                        normalIcon = new int[]{R.mipmap.tk_icon_bottom_home};
                        selectIcon = new int[]{R.mipmap.tk_icon_bottom_home_sel};
                    }
                }
            }
        } else {
            fragments.add(new TkFrontPhotosFragment());
            if (VideoApplication.getInstance().isApplyFrontHomeMessage()) {
                fragments.add(new TkFrontMessageFragment());
                if (VideoApplication.getInstance().isApplyFrontHomeMine()) {
                    fragments.add(new TkFrontMineFragment());
                    tabText = new String[]{"", "", ""};
                    normalIcon = new int[]{R.mipmap.tk_icon_bottom_photos, R.mipmap.tk_icon_bottom_message, R.mipmap.tk_icon_bottom_mine};
                    selectIcon = new int[]{R.mipmap.tk_icon_bottom_photos_sel, R.mipmap.tk_icon_bottom_message_sel, R.mipmap.tk_icon_bottom_mine_sel};
                } else {
                    tabText = new String[]{"", ""};
                    normalIcon = new int[]{R.mipmap.tk_icon_bottom_photos, R.mipmap.tk_icon_bottom_message};
                    selectIcon = new int[]{R.mipmap.tk_icon_bottom_photos_sel, R.mipmap.tk_icon_bottom_message_sel};
                }
            } else {
                if (VideoApplication.getInstance().isApplyFrontHomeMine()) {
                    fragments.add(new TkFrontMineFragment());
                    tabText = new String[]{"", ""};
                    normalIcon = new int[]{R.mipmap.tk_icon_bottom_photos, R.mipmap.tk_icon_bottom_mine};
                    selectIcon = new int[]{R.mipmap.tk_icon_bottom_photos_sel, R.mipmap.tk_icon_bottom_mine_sel};
                }
            }
        }

        for (int i = 0; i < tabText.length; i++) {
            mTabEntities.add(new TabEntity(tabText[i], selectIcon[i], normalIcon[i]));
        }
        binding.bottom.setBackgroundResource(VideoApplication.getInstance().getFrontPageBottomBgColor());
        binding.bottom.setTabData(mTabEntities, this, R.id.fragment, fragments);
        for (int i = 0; i < fragments.size(); i++) {
            binding.bottom.getTitleView(i).setVisibility(View.GONE);
        }
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