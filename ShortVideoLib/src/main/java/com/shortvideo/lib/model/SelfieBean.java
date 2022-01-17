package com.shortvideo.lib.model;

public class SelfieBean {

    private String videoPath;
    private String videoThum;

    public SelfieBean(String videoPath, String videoThum) {
        this.videoPath = videoPath;
        this.videoThum = videoThum;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoThum() {
        return videoThum;
    }

    public void setVideoThum(String videoThum) {
        this.videoThum = videoThum;
    }
}
