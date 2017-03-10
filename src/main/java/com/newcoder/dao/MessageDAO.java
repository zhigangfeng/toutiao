package com.newcoder.dao;

import com.newcoder.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
@Mapper
public interface MessageDAO {
    String TABLE_NAME="message";
    String INSERT_FIELDS="from_id,to_id,content,has_read,conversation_id,created_date";
    String SELECT_FIELDS="id,"+INSERT_FIELDS;
    @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") " ,
            "values (#{fromId},#{toId},#{content},#{hasRead},#{conversationId},#{createdDate})"})
    int addMessage(Message message);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where conversation_id=#{conversationId} order by id desc limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId, @Param("offset") int offset,@Param("limit") int limit);

    //第一步：将用户有关的会话选择出来按照日期排序，单独成表
    //第二步：根据会话ID，分组
    //地散步：求出每组会话的和，以降序排序
    @Select({"select",INSERT_FIELDS,",count(id) as id from(select * from ",TABLE_NAME,"where from_id=#{userId} or to_id=#{userId} order by id desc) tt group by conversation_id order by id desc limit #{offset},#{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,@Param("offset") int offset,@Param("limit") int limit);

    @Select({"select count(id) from",TABLE_NAME,"where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("userId") int userId,@Param("conversationId") String conversationId);
}
