package com.shortvideo.lib.common.event;

public class OnOutVideoEvent {

    private int id;
    private String f;

    public OnOutVideoEvent(int id, String f) {
        this.id = id;
        this.f = f;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }
}
