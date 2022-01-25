package com.shortvideo.lib.ui.activity.concise.ncindex;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.shortvideo.lib.R;
import com.shortvideo.lib.VideoApplication;
import com.shortvideo.lib.databinding.TkActivityNcindexBinding;
import com.shortvideo.lib.ui.activity.concise.ncindex.interfaces.Resourceble;
import com.shortvideo.lib.ui.activity.concise.ncindex.interfaces.ScreenShotable;
import com.shortvideo.lib.ui.activity.concise.ncindex.model.SlideMenuItem;
import com.shortvideo.lib.ui.activity.concise.ncindex.util.ViewAnimator;
import com.shortvideo.lib.utils.ActivityManager;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.ViewAnimationUtils;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class NcIndexActivity extends AppCompatActivity implements ViewAnimator.ViewAnimatorListener {

    TkActivityNcindexBinding binding;

    private ActionBarDrawerToggle drawerToggle;
    private final List<SlideMenuItem> list = new ArrayList<>();
    private ViewAnimator viewAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .navigationBarColor(R.color.white)
                .statusBarDarkFont(true, 0f)
                .init();

        binding = TkActivityNcindexBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        NcIndexFragment ncIndexFragment = NcIndexFragment.newInstance(VideoApplication.getInstance().isApplyFrontHomeVideo() ?
                NcIndexFragment.VIDEO : NcIndexFragment.WALLPAPER);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, ncIndexFragment)
                .commit();

        binding.drawerLayout.setScrimColor(Color.TRANSPARENT);
        binding.leftDrawer.setOnClickListener(view -> binding.drawerLayout.closeDrawers());

        setActionBar();
        createMenuList();
        viewAnimator = new ViewAnimator<>(this, list, ncIndexFragment, binding.drawerLayout, this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void setActionBar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                binding.drawerLayout,         /* DrawerLayout object */
                binding.toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                binding.leftDrawer.removeAllViews();
                binding.leftDrawer.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && binding.leftDrawer.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        binding.drawerLayout.addDrawerListener(drawerToggle);
    }

    private void createMenuList() {
        list.add(new SlideMenuItem(NcIndexFragment.CLOSE, R.mipmap.tk_icon_ncindex_left_close));
        if (VideoApplication.getInstance().isApplyFrontHomeVideo())
            list.add(new SlideMenuItem(NcIndexFragment.VIDEO, R.drawable.tk_menu_item_selector));
        list.add(new SlideMenuItem(NcIndexFragment.WALLPAPER, R.drawable.tk_menu_item_selector2));
        list.add(new SlideMenuItem(NcIndexFragment.PHOTOS, R.drawable.tk_menu_item_selector3));
        list.add(new SlideMenuItem(NcIndexFragment.MESSAGE, R.drawable.tk_menu_item_selector4));
        list.add(new SlideMenuItem(NcIndexFragment.MINE, R.drawable.tk_menu_item_selector5));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private ScreenShotable replaceFragment(String name, ScreenShotable screenShotable, int topPosition) {
        int finalRadius = Math.max(binding.contentFrame.getWidth(), binding.contentFrame.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(binding.contentFrame, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                binding.contentOverlay.setVisibility(View.VISIBLE);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) binding.contentOverlay.getLayoutParams();
                layoutParams.setMargins(0, screenShotable.getTopHeight(), 0, 0);
                binding.contentOverlay.setLayoutParams(layoutParams);
                binding.contentOverlay.setBackground(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                binding.contentOverlay.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();

        NcIndexFragment ncIndexFragment = NcIndexFragment.newInstance(name);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, ncIndexFragment)
                .commit();
        return ncIndexFragment;
    }

    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        if (slideMenuItem.getName().equals(NcIndexFragment.CLOSE)) {
            return screenShotable;
        } else {
            return replaceFragment(slideMenuItem.getName(), screenShotable, position);
        }
    }

    @Override
    public void disableHomeButton() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public void enableHomeButton() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setHomeButtonEnabled(true);
        binding.drawerLayout.closeDrawers();
    }

    @Override
    public void addViewToContainer(View view) {
        binding.leftDrawer.addView(view);
    }
}
