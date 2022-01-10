package com.shortvideo.lib.ui.adapter;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.shortvideo.lib.R;
import com.shortvideo.lib.model.HomeBean;
import com.shortvideo.lib.ui.widgets.StdTikTok;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;

import java.util.List;

public class StdTikTokAdapter extends BaseQuickAdapter<HomeBean.DataDTO, BaseViewHolder> {

    public StdTikTokAdapter(@Nullable List<HomeBean.DataDTO> data) {
        super(R.layout.tk_item_tiktok, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, HomeBean.DataDTO workBean) {
        showAndHide(baseViewHolder, workBean);
    }

    private void showAndHide(BaseViewHolder baseViewHolder, HomeBean.DataDTO workBean) {
        StdTikTok jzvdStd = baseViewHolder.getView(R.id.videoplayer);
        String proxyUrl;
        int type = workBean.getType();

        if (type != 2) {
            jzvdStd.setLink("");
            jzvdStd.setPosition(baseViewHolder.getLayoutPosition());
            baseViewHolder.setText(R.id.like_num, workBean.getVideo().getLike_count() + "")
                    .setImageResource(R.id.like, workBean.getVideo().isIs_like() ? R.mipmap.icon_liked : R.mipmap.icon_like)
                    .setText(R.id.title, workBean.getVideo().getTitle())
                    .setText(R.id.aut, workBean.getVideo().getAuthor())
                    .setText(R.id.content, workBean.getVideo().getDesc())
                    .setVisible(R.id.like_num, workBean.getVideo().getLike_count() > 0)
                    .setGone(R.id.adImage, true)
                    .setGone(R.id.aut, TextUtils.isEmpty(workBean.getVideo().getAuthor()))
                    .setGone(R.id.content, TextUtils.isEmpty(workBean.getVideo().getDesc()))
                    .setGone(R.id.videoplayer, false)
//                    .setGone(R.id.before, false)
                    .setGone(R.id.ll_btn, false)
                    .setGone(R.id.ll_title, false)
                    .setGone(R.id.ll_ext, true)
                    .setGone(R.id.ll_ext_big, true);
//                    .setGone(R.id.setting, false);

            proxyUrl = workBean.getVideo().getUrl();
            jzvdStd.setBefore(workBean.getVideo().getThumburl());

            GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
            gsyVideoOption.setIsTouchWiget(false)
                    .setRotateViewAuto(false)
                    .setRotateWithSystem(false)
                    .setLockLand(false)
                    .setAutoFullWithSize(true)
                    .setShowFullAnimation(false)
                    .setLooping(true)
                    .setNeedLockFull(false)
                    .setUrl(proxyUrl)
                    .setCacheWithPlay(true)
                    .setVideoAllCallBack(new GSYSampleCallBack() {

                        @Override
                        public void onStartPrepared(String url, Object... objects) {
                            super.onStartPrepared(url, objects);
                        }

                        @Override
                        public void onPrepared(String url, Object... objects) {
                            super.onPrepared(url, objects);
                            //设置 seek 的临近帧。
//                            jzvdStd.postDelayed(() -> {
//                                if (jzvdStd.getGSYVideoManager().getPlayer() instanceof Exo2PlayerManager &&
//                                        ((Exo2PlayerManager) jzvdStd.getGSYVideoManager().getPlayer()) != null)
//                                    ((Exo2PlayerManager) jzvdStd.getGSYVideoManager().getPlayer()).setSeekParameter(SeekParameters.NEXT_SYNC);
//                            }, 100);
                        }

                        @Override
                        public void onEnterFullscreen(String url, Object... objects) {
                            super.onEnterFullscreen(url, objects);
                        }

                        @Override
                        public void onAutoComplete(String url, Object... objects) {
                            super.onAutoComplete(url, objects);
                        }

                        @Override
                        public void onClickStartError(String url, Object... objects) {
                            super.onClickStartError(url, objects);
                        }

                        @Override
                        public void onQuitFullscreen(String url, Object... objects) {
                            super.onQuitFullscreen(url, objects);
                        }
                    })
                    .setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> {

                        if (jzvdStd.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING)
                            jzvdStd.setProgresses(progress);
                    })
                    .build(jzvdStd);
        } else {
            baseViewHolder.setText(R.id.title, workBean.getBanner().getTitle())
                    .setText(R.id.ad_dec, workBean.getBanner().getDesc())
                    .setText(R.id.ad_title, workBean.getBanner().getTitle())
                    .setText(R.id.ad_content, workBean.getBanner().getLink())
                    .setText(R.id.small_ad_title, workBean.getBanner().getTitle())
                    .setText(R.id.small_ad_content, workBean.getBanner().getLink())
                    .setGone(R.id.videoplayer, true)
                    .setGone(R.id.ad_tip, !workBean.getBanner().isShow())
                    .setGone(R.id.adImage, true)
                    .setGone(R.id.ad_dec, TextUtils.isEmpty(workBean.getBanner().getDesc()))
                    .setGone(R.id.ll_btn, true)
                    .setGone(R.id.ll_title, true)
                    .setGone(R.id.ll_ext, false)
                    .setGone(R.id.ll_ext_big, workBean.getBanner().isAdShow())
                    .setGone(R.id.ll_ext_big, true);
//                    .setGone(R.id.setting, true);

            if (!TextUtils.isEmpty(workBean.getBanner().getIcon())) {
                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                Glide.with(getContext())
                        .load(workBean.getBanner().getIcon())
                        .dontAnimate()
                        .apply(options)
                        .into((ImageView) baseViewHolder.getView(R.id.ad_img));

                Glide.with(getContext())
                        .load(workBean.getBanner().getIcon())
                        .dontAnimate()
                        .apply(options)
                        .into((ImageView) baseViewHolder.getView(R.id.small_ad_img));
            }

            jzvdStd.setBefore("");
            jzvdStd.setLink(workBean.getBanner().getLink());
            jzvdStd.setBannerId(workBean.getBanner().getId(), workBean.getBanner().getBanner_type());

            jzvdStd.setOnClickListener(v -> {
                Uri content_url = Uri.parse(workBean.getBanner().getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
                getContext().startActivity(intent);
                jzvdStd.reportBannerClick(workBean.getBanner().getId(), 2);
            });

            ImageView ad = baseViewHolder.getView(R.id.adImage);
            ad.setOnClickListener(v -> {
                Uri content_url = Uri.parse(workBean.getBanner().getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
                getContext().startActivity(intent);
                jzvdStd.reportBannerClick(workBean.getBanner().getId(), 1);
            });

            if (workBean.getBanner().getBanner_type() == 2) {
                baseViewHolder.setGone(R.id.adImage, false)
                        .setGone(R.id.videoplayer, true);

                jzvdStd.release();

                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                Glide.with(getContext())
                        .load(workBean.getBanner().getUrl())
                        .dontAnimate()
                        .apply(options)
                        .into((ImageView) baseViewHolder.getView(R.id.adImage));
            } else {
                baseViewHolder.setGone(R.id.adImage, true)
                        .setGone(R.id.videoplayer, false);

                proxyUrl = workBean.getBanner().getUrl();
                jzvdStd.setBefore("");

                GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
                gsyVideoOption.setIsTouchWiget(false)
                        .setRotateViewAuto(false)
                        .setRotateWithSystem(false)
                        .setLockLand(false)
                        .setAutoFullWithSize(true)
                        .setShowFullAnimation(false)
                        .setLooping(true)
                        .setNeedLockFull(false)
                        .setUrl(proxyUrl)
                        .setCacheWithPlay(true)
                        .setVideoAllCallBack(new GSYSampleCallBack() {
                            @Override
                            public void onPrepared(String url, Object... objects) {
                                super.onPrepared(url, objects);

                                //设置 seek 的临近帧。
//                                jzvdStd.postDelayed(() -> {
//                                    if (jzvdStd.getGSYVideoManager().getPlayer() instanceof Exo2PlayerManager)
//                                        ((Exo2PlayerManager) jzvdStd.getGSYVideoManager().getPlayer()).setSeekParameter(SeekParameters.NEXT_SYNC);
//                                }, 100);
                            }

                            @Override
                            public void onEnterFullscreen(String url, Object... objects) {
                                super.onEnterFullscreen(url, objects);
                            }

                            @Override
                            public void onAutoComplete(String url, Object... objects) {
                                super.onAutoComplete(url, objects);
                            }

                            @Override
                            public void onClickStartError(String url, Object... objects) {
                                super.onClickStartError(url, objects);
                            }

                            @Override
                            public void onQuitFullscreen(String url, Object... objects) {
                                super.onQuitFullscreen(url, objects);
                            }
                        })
//                            .setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> Logger.e("播放进度: " + progress + " / " + secProgress + " / " + currentPosition))
                        .build(jzvdStd);
            }
        }
    }
}
