package com.shortvideo.lib.common.event;

public class OnFirebaseDataEvent {

    /**
     * 1.跳转视频App   2.其他App   3.已经更新到其他APP
     **/
    private int type;

    public OnFirebaseDataEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
