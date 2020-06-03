package com.nowcoder.service;

import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    //关注
    public boolean follow(int userId,int entityId,int entityType){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
         //redis开启事务
        Transaction tx = jedisAdapter.multi(jedis);
        //将关注事件add到sortedSets中
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityType));
         //redis执行事务
        List<Object> ret = jedisAdapter.exec(tx,jedis);
        return ret.size()==2&&(Long)ret.get(0)>0&&(Long)ret.get(1)>0;
    }

    //取消关注
    public boolean unfollow(int userId,int entityId,int entityType){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zrem(followerKey,String.valueOf(userId));
        tx.zrem(followeeKey,String.valueOf(entityType));
        List<Object> ret = jedisAdapter.exec(tx,jedis);
        return ret.size()==2&&(Long)ret.get(0)>0&&(Long)ret.get(1)>0;
    }

    //将set转成list
    public List<Integer> getIdsFromSet(Set<String> idset){
        List<Integer> list = new ArrayList<>();
        for (String ids : idset) {
            list.add(Integer.parseInt(ids));
        }
        return list;
    }

    //获取粉丝：没有分页
    public List<Integer> getFollowers(int entityId,int entityType,int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrange(followerKey,0,count));
    }

    //获取粉丝：分页
    public List<Integer> getFollowers(int entityId,int entityType,int offset,int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrange(followerKey,offset,count));
    }

    //获取一个实体的关注者：没有分页
    public List<Integer> getFollowees(int entityId,int entityType,int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,0,count));
    }

    //获取一个实体的关注者：分页
    public List<Integer> getFollowees(int entityId,int entityType,int offset,int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,offset,count));
    }

    //获取一个实体的粉丝数量
   public long getFollowerCount(int entityType,int entityId){
       String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
       return jedisAdapter.zcard(followerKey);
   }

    //获取一个用户关注实体的数量
    public long getFolloweeCount(int entityId,int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    //判断该用户是不是这个实体的粉丝
    public boolean isFollower(int userId,int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId))!=null;
    }
}
