package com.shortvideo.lib.common.event;

public class OnVideoDoubleLikeEvent {

    private int position;

    public OnVideoDoubleLikeEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
