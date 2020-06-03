package com.nowcoder.dao;

import com.nowcoder.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface FeedDAO {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = "user_id, data, created_date, type";
    String SELECT_FIELDS = "id,"+INSERT_FIELDS;

    //增加一个新鲜事
    @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,
            ")values(#{userId},#{data},#{createdDate},#{type})"})
    int addFeed(Feed feed);

    //拉模式：拉取关注的所有用户的新鲜事
    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);

    //查询一个新鲜事
    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where id=#{id}"})
    Feed getFeedById(int id);
}
