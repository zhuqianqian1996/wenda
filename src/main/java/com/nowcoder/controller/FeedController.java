package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.Feed;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.User;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowService;
import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {
    @Autowired
    FeedService feedService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;

    //拉的模式:拉取当前对象关注的人的新鲜事分页展示
    @RequestMapping(path = {"/pullfeeds"}, method = {RequestMethod.GET})
    public String getPullFeeds(Model model){
        User localHost = hostHolder.getUser();
        List<Integer> folowees = new ArrayList<>();
        //登录状态
        if (localHost!=null){
            //当前用户的关注者
            folowees = followService.getFollowees(hostHolder.getUser().getId(), EntityType.ENTITY_USER,Integer.MAX_VALUE);
        }
        //拉取所有用户的新鲜事
        List<Feed> userFeeds = feedService.getUserFeeds(Integer.MAX_VALUE, folowees, 10);
        model.addAttribute("feeds",userFeeds);
        return "feeds";
 }

    //推的模式，把新鲜事推送给关注该用户的粉丝
    @RequestMapping(path = {"/pushfeeds"}, method = {RequestMethod.GET})
    public String getPushFeeds(Model model){
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        String followerkey = RedisKeyUtil.getTimelineKey(localUserId);
        //取出新鲜事
        List<String> feedIds = jedisAdapter.lrange(followerkey, 0, 10);
        ArrayList<Feed> feeds = new ArrayList<>();
        for (String feedId : feedIds) {
            Feed feed = feedService.getFeedById(Integer.parseInt(feedId));
            if (feed==null){
                continue;
            }
            feeds.add(feed);
        }
        model.addAttribute("feeds",feeds);
        return "feeds";
    }
}
