package com.nowcoder.async;


import java.util.List;

public interface EventHandler {
     //事件处理函数
     void doHandle(EventModel model);

     //获取该事件处理器所支持的事件类型
     List<EventType> getSupportEventTypes();
}
