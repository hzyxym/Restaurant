package com.hzy.restaurant.bean.event;

public class MsgEvent<T> {
    public int type;
    public T data;
    public String mac;

    public MsgEvent(int type) {
        this.type = type;
    }

    public MsgEvent(int type, T data) {
        this.type = type;
        this.data = data;
    }

    public MsgEvent(String mac, int type, T data) {
        this.mac = mac;
        this.type = type;
        this.data = data;
    }
}
