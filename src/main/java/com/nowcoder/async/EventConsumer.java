package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventConsumer implements InitializingBean , ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    //创建一个类型为Map<EventType, List<EventHandler>>的map，用于存放所有的Handler。
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    //spring上下文
    private ApplicationContext applicationContext;

    //Consumer获取applicationContext
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext ;
    }


    //spring初始化Consumer的时候就会执行该方法。
    //实现了InitializingBean后，Spring启动以后会在初始化bean(Consumer)后执行afterPropertiesSet()方法。
    @Override
    public void afterPropertiesSet() throws Exception {
        relateHandler();
        consumerEvent();
    }

    //取出事件关联的handle
    private void relateHandler(){
        // 我们通过applicationContext获取实现了EventHandler接口的全部Handler。
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans!=null){
            //通过for循环，获取map中所有的key-value的集合
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                //找到EventHandler关注的是哪些类型的事件
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                for (EventType type : eventTypes) {
                    //判断eventType是否为空,不为空则分配一个处理器
                    if (!config.containsKey(type)){
                        config.put(type,new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }
    }

    private void consumerEvent(){
        //启动线程去消化事件，可以同时启动多个线程去消费事件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //该线程使用死循环让其不间断的运行。
                while (true) {
                    //这里的key就是producer的key
                    String key = RedisKeyUtil.getEventQueueKey();
                    //用brpop把redis中的事件拉出来
                    List<String> events = jedisAdapter.brpop(0, key);
                    //过滤掉key之后，剩下value，把value用JSON的api转化为EventModel
                    for (String event : events) {
                        if (event.equals(key)) {
                            continue;
                        }
                        //把value用JSON的api转化为EventModel
                        EventModel eventModel = JSON.parseObject(event, EventModel.class);
                        //在map中寻找是否有能处理EventModel的Handler，判断方法是看EventType是否支持。
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别的事件");
                            continue;
                        }
                        //过滤掉不支持的EventType之后，调用每一个支持该EventType的doHandle方法。
                        for (EventHandler eventHandler : config.get(eventModel.getType())) {
                            eventHandler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }
}
