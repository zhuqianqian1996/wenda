package com.nowcoder.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;


@Component
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    //设置redis线程池
    private JedisPool pool;

    public static void print(int index,Object obj){
        System.out.println(String.format("%d, %s",index,obj.toString()));
    }

     public static void main(String[] argv){
         Jedis jedis = new Jedis("redis://localhost:6379/9");
         jedis.flushDB();

         //get set
         jedis.set("hello","world");
         print(1,jedis.get("hello"));
         //重命名
         jedis.rename("hello","newhello");
         //增加一个过期时间
         jedis.setex("hello2",10,"hello2");

         //数值型：验证码、pv、缓存
         jedis.set("pv","100");
         print(2,jedis.get("pv"));
         //数值+1
         print(3,jedis.incr("pv"));
         //数值+指定数值
         print(4,jedis.incrBy("pv",5));
         //                                                数值-1
         print(5,jedis.decr("pv"));
         //数值-指定数值
         print(6,jedis.decrBy("pv",2));
         //查询redis里面目前的key
         print(7,jedis.keys("p*"));

         //list：最新列表，关注列表
         String listName = "list";
         for (int i=0;i<10;i++){
             jedis.lpush(listName,"a"+String.valueOf(i));
         }
         print(8,jedis.lrange(listName,0,12));
         print(8,jedis.lrange(listName,0,3));//8, [a9, a8, a7, a6]
         //list中的长度
         print(9,jedis.llen(listName));//10
         //弹出左边的第一个数
         print(9,jedis.lpop(listName));
         print(9,jedis.llen(listName));//9, 9
         print(9,jedis.lrange(listName,0,12));
         print(10,jedis.lrange(listName,2,5));
         print(10,jedis.lindex(listName,3));//10, a5
         print(10,jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER,"a4","xx"));
         print(10,jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE,"a4","bb"));
         print(11,jedis.lrange(listName,0,12));

         //hash：对象属性，不定长属性数
         String userkey = "userxx";
         jedis.hset(userkey,"name","jim");
         jedis.hset(userkey,"age","12");
         jedis.hset(userkey,"phone","123131321");
         print(12,jedis.hget(userkey,"name"));
         print(13,jedis.hgetAll(userkey));
         jedis.hdel(userkey,"phone");
         print(14,jedis.hgetAll(userkey));
         print(15,jedis.hexists(userkey,"email"));//false
         print(16,jedis.hexists(userkey,"age"));//true
         print(17,jedis.hkeys(userkey));
         print(17,jedis.hvals(userkey));
         jedis.hsetnx(userkey,"school","zju");
         jedis.hsetnx(userkey,"name","yxy");
         print(18,jedis.hgetAll(userkey));//{school=zju, name=jim, age=12}

         //set：点赞点踩，抽奖，已读，共同好友
         String likekey1 = "commentlike1";
         String likekey2 = "commentlike2";
         for (int i=0;i<10;++i){
             jedis.sadd(likekey1,String.valueOf(i));
             jedis.sadd(likekey2,String.valueOf(i*i));
         }
         print(19,jedis.smembers(likekey1));
         print(19,jedis.smembers(likekey2));
         print(20,jedis.sunion(likekey1,likekey2));
         //第一个集合有，第二个集合没有的元素
         print(21,jedis.sdiff(likekey1,likekey2));
         //交
         print(22,jedis.sinter(likekey1,likekey2));
         //判断集合里面有没有指定元素
         print(23,jedis.sismember(likekey1,"12"));//false
         print(23,jedis.sismember(likekey2,"16"));//true
         //移除某一个元素
         jedis.srem(likekey1,"5");
         print(24,jedis.smembers(likekey1));//[0, 1, 2, 3, 4, 6, 7, 8, 9]
         //将likekey2中的元素移到likekey1
         jedis.smove(likekey2,likekey1,"25");
         print(25,jedis.smembers(likekey1));//[0, 1, 2, 3, 4, 6, 7, 8, 9, 25]
         //查看集合中的元素个数
         print(26,jedis.scard(likekey1));
         //随机获取一个元素
         print(26,jedis.srandmember(likekey1));

         //SortedSet 优先队列，排行榜
         String rankKey = "rankKey";
         jedis.zadd(rankKey, 15, "jim");
         jedis.zadd(rankKey, 60, "Ben");
         jedis.zadd(rankKey, 90, "Lee");
         jedis.zadd(rankKey, 75, "Lucy");
         jedis.zadd(rankKey, 80, "Mei");
         //查找优先队列中的数据数量
         print(27,jedis.zcard(rankKey));
         //查找及格的人数
         print(28,jedis.zcount(rankKey,61,100));
         //查找某一个人的分数,可以查询排行榜上某一个key的分数是多少
         print(29,jedis.zscore(rankKey,"Lucy"));//75.0
         //给某个key的值加分
         jedis.zincrby(rankKey,4,"Lucy");
         print(30,jedis.zscore(rankKey,"Lucy"));
         //如果本身优先队列中没有这个key,默认是0分
         jedis.zincrby(rankKey,2,"Luc");
         print(31,jedis.zscore(rankKey,"Luc"));
         print(32,jedis.zrange(rankKey,0,100));//[Luc, jim, Ben, Lucy, Mei, Lee]
         print(33,jedis.zrange(rankKey,0,10));
         //优先队列默认从小到大排序
         print(34, jedis.zrange(rankKey,1,3));
         //从大到小排序
         print(35,jedis.zrevrange(rankKey,1,3));
         //循环打印60-100分之间的元素(从小到大)
         for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey,"0","100")){
             print(37,tuple.getElement()+":"+tuple.getScore());
         }
         //查询指定key的当前排名
         print(38,jedis.zrank(rankKey,"Ben"));
         print(39,jedis.zrevrank(rankKey,"Ben"));

         //分值一样(按照字典序来排序)
         String setKey = "zset";
         jedis.zadd(setKey,1,"a");
         jedis.zadd(setKey,1,"b");
         jedis.zadd(setKey,1,"c");
         jedis.zadd(setKey,1,"d");
         jedis.zadd(setKey,1,"e");
         //负无穷-正无穷的数量
         print(40,jedis.zlexcount(setKey,"-","+"));
         //开集合和闭集合
         print(41,jedis.zlexcount(setKey,"(b","[d"));
         print(42,jedis.zlexcount(setKey,"[b","[d"));
         //删掉b
         jedis.zrem(setKey,"b");
         print(43,jedis.zrange(setKey,0,10));
         //删掉C以上的
         jedis.zremrangeByLex(setKey,"(c","+");
         print(44,jedis.zrange(setKey,0,2));//[a, c]

         //redis连接池
/*
        JedisPool pool = new JedisPool();
         for (int i = 0; i < 100 ; ++i){
             Jedis j = pool.getResource();
             print(45,j.get("pv"));
             j.close();
         }
*/

         //redie缓存user(被序列化了)

         User user = new User();
         user.setName("xx");
         user.setPassword("ppp");
         user.setHeadUrl("a.png");
         user.setSalt("salt");
         user.setId(1);
         print(46,JSONObject.toJSONString(user));
         jedis.set("user1",JSONObject.toJSONString(user));

         //取出序列化的对象
         String value = jedis.get("user1");
         User user2 = JSON.parseObject(value, User.class);
         print(47,user2);
     }

     //afterPropertiesSet方法在初始化bean的时候对某一个具体的bean进行配置
    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/10");
    }

    //向集合中添加元素
    public long sadd(String key,String value){
        Jedis jedis = null;
        try {
             jedis = pool.getResource();
             return jedis.sadd(key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    //移除集合中的元素
    public long srem(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    //获取集合中元素的数量
    public long scard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    //判断是否存在元素
    public boolean sismember(String key,String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    //lpush
    public long lpush(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public List<String> brpop(int timeout, String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout,key);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return null;
    }



    public Jedis getJedis(){
        return pool.getResource();
    }

    //jedis开启事务
    public Transaction multi(Jedis jedis){
        try {
            return jedis.multi();
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        return null;
    }

    //执行事务
    public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            tx.discard();
        } finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (IOException ioe) {
                    logger.error("发生异常" + ioe.getMessage());
                }
            }

            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zadd(String key,String value,double score){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key,score,value);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public Set<String> zrange(String key, int start, int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key,start,end);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return null;
    }

    public Set<String> zrevrange(String key, int start, int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key,start,end);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return null;
    }

    public Long zcard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return 0L;
    }


    public Double zscore(String key, String member){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key,member);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return null;
    }


    public List<String> lrange(String key, int start,int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key,start,end);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return null;
    }


}
