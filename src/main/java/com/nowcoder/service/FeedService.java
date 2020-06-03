package com.nowcoder.service;

import com.nowcoder.dao.FeedDAO;
import com.nowcoder.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {
    @Autowired
    FeedDAO feedDAO;

    //增加一个新鲜事
    public boolean addFeed(Feed feed){
        feedDAO.addFeed(feed);
        return feed.getId()>0;
}

    //拉模式：拉取关注的所有用户的新鲜事
    public List<Feed>  getUserFeeds(int maxId,List<Integer> UserIds,int count){
        return feedDAO.selectUserFeeds(maxId, UserIds,count);
    }

    //查询一个新鲜事
    public Feed getFeedById(int id){
        return feedDAO.getFeedById(id);
    }
}
