package com.nowcoder.async;

public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    UNFOLLOW(5);

    //事件类型标识符
    private int value;

    EventType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
