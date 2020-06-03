package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    //异步消息队列的入口，由EventProducer统一发送事件（保存到队列里面）
    public boolean fireEvent(EventModel eventModel){
        try {
            //eventModel转成json字符串
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            //把事件发送给队列
            jedisAdapter.lpush(key,json);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
