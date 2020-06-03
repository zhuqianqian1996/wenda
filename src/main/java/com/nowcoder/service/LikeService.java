package com.nowcoder.service;

import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    //获取点赞的人数
    public long getLikeCount(int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return jedisAdapter.scard(likeKey);
    }

    //赞和踩的状态（高亮）：用户对一个评论的喜欢或者踩的状态
    public long getLikeStatus(int userId,int entityType,int entityId){
        //确定key的值
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if (jedisAdapter.sismember(likeKey,String.valueOf(userId))){
            return 1;
        }
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityType, entityId);
          return jedisAdapter.sismember(dislikeKey,String.valueOf(userId))?-1:0;
    }


    //赞
    public long like(int userId,int entityType,int entityId){
        //赞的用户id加到redis
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeKey,String.valueOf(userId));
        //把踩的用户从redis中删掉
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityType, entityId);
        jedisAdapter.srem(dislikeKey,String.valueOf(userId));
        //返回点赞的人数
        return jedisAdapter.scard(likeKey);
    }

    //踩
    public long dislike(int userId,int entityType,int entityId){
        //踩的用户id加到redis
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityType, entityId);
        jedisAdapter.sadd(dislikeKey,String.valueOf(userId));
        //把赞的用户从redis中删掉
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey,String.valueOf(userId));
        //返回点赞的人数
        return jedisAdapter.scard(likeKey);
    }
}
