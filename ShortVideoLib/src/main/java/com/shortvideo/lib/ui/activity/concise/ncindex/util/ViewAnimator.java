package com.shortvideo.lib.ui.activity.concise.ncindex.util;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.noober.background.drawable.DrawableCreator;
import com.shortvideo.lib.R;
import com.shortvideo.lib.ui.activity.concise.ncindex.animation.FlipAnimation;
import com.shortvideo.lib.ui.activity.concise.ncindex.interfaces.Resourceble;
import com.shortvideo.lib.ui.activity.concise.ncindex.interfaces.ScreenShotable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konstantin on 12.01.2015.
 */
public class ViewAnimator<T extends Resourceble> {
    private final int ANIMATION_DURATION = 175;
    public static final int CIRCULAR_REVEAL_ANIMATION_DURATION = 500;

    private final AppCompatActivity appCompatActivity;

    private final List<T> list;

    private final List<View> viewList = new ArrayList<>();
    private ScreenShotable screenShotable;
    private final DrawerLayout drawerLayout;
    private final ViewAnimatorListener animatorListener;

    public ViewAnimator(AppCompatActivity activity,
                        List<T> items,
                        ScreenShotable screenShotable,
                        final DrawerLayout drawerLayout,
                        ViewAnimatorListener animatorListener) {
        this.appCompatActivity = activity;

        this.list = items;
        this.screenShotable = screenShotable;
        this.drawerLayout = drawerLayout;
        this.animatorListener = animatorListener;
    }

    public void showMenuContent() {
        setViewsClickable(false);
        viewList.clear();
        double size = list.size();
        for (int i = 0; i < size; i++) {
            View viewMenu = appCompatActivity.getLayoutInflater().inflate(R.layout.tk_ncindex_menu_list_item, null);
            if (i == 0) {
                Drawable drawableOne = new DrawableCreator.Builder()
                        .setCornersRadius(0, 0, 0, 60)
                        .setSolidColor(Color.parseColor("#181724"))
                        .build();
                viewMenu.setBackground(drawableOne);
            } else if (i == size - 1) {
                Drawable drawableLast = new DrawableCreator.Builder()
                        .setCornersRadius(0, 60, 0, 0)
                        .setSolidColor(Color.parseColor("#7f181724"))
                        .build();
                viewMenu.setBackground(drawableLast);
            } else {
                viewMenu.setBackgroundColor(Color.parseColor("#7f181724"));
            }

            final int finalI = i;
            viewMenu.setOnClickListener(v -> {
                int[] location = {0, 0};
                v.getLocationOnScreen(location);
                switchItem(list.get(finalI), location[1] + v.getHeight() / 2);
            });
            ((ImageView) viewMenu.findViewById(R.id.menu_item_image)).setImageResource(list.get(i).getImageRes());
            viewMenu.setVisibility(View.GONE);
            viewMenu.setEnabled(false);
            viewList.add(viewMenu);
            animatorListener.addViewToContainer(viewMenu);
            final double position = i;
            final double delay = 3 * ANIMATION_DURATION * (position / size);
            new Handler().postDelayed(() -> {
                if (position < viewList.size()) {
                    animateView((int) position);
                }
                if (position == viewList.size() - 1) {
                    screenShotable.takeScreenShot();
                    screenShotable.takeTopHeight();
                    setViewsClickable(true);
                }
            }, (long) delay);
        }

    }

    private void hideMenuContent() {
        setViewsClickable(false);
        double size = list.size();
        for (int i = list.size(); i >= 0; i--) {
            final double position = i;
            final double delay = 3 * ANIMATION_DURATION * (position / size);
            new Handler().postDelayed(() -> {
                if (position < viewList.size()) {
                    animateHideView((int) position);
                }
            }, (long) delay);
        }

    }

    private void setViewsClickable(boolean clickable) {
        animatorListener.disableHomeButton();
        for (View view : viewList) {
            view.setEnabled(clickable);
        }
    }

    private void animateView(int position) {
        final View view = viewList.get(position);
        view.setVisibility(View.VISIBLE);
        FlipAnimation rotation =
                new FlipAnimation(90, 0, 0.0f, view.getHeight() / 2.0f);
        rotation.setDuration(ANIMATION_DURATION);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(rotation);
    }

    private void animateHideView(final int position) {
        final View view = viewList.get(position);
        FlipAnimation rotation =
                new FlipAnimation(0, 90, 0.0f, view.getHeight() / 2.0f);
        rotation.setDuration(ANIMATION_DURATION);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(View.INVISIBLE);
                if (position == viewList.size() - 1) {
                    animatorListener.enableHomeButton();
                    drawerLayout.closeDrawers();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(rotation);
    }

    private void switchItem(Resourceble slideMenuItem, int topPosition) {
        this.screenShotable = animatorListener.onSwitch(slideMenuItem, screenShotable, topPosition);
        hideMenuContent();
    }

    public interface ViewAnimatorListener {

        ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position);

        void disableHomeButton();

        void enableHomeButton();

        void addViewToContainer(View view);

    }
}
