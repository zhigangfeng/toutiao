package com.newcoder.dao;

import com.newcoder.model.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * Created by Administrator on 2016/12/23.
 */
    @Mapper
    public interface LoginTicketDAO {
        String TABLE_NAME = "login_ticket";
        String INSERT_FIELDS = "user_id,expired,status,ticket ";//少了个   --->   ,这个，不应该往这行代码写
        String SELECT_FIELDS = "id"+","+INSERT_FIELDS;//要写在这里,   不然SQL语句有问题，就是少些了一个逗号对么YESen我再试一下

        @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,
                ") Values(#{userId},#{expired},#{status},#{ticket})"})
        int addTicket(LoginTicket ticket);

        @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where ticket=#{ticket}"})
        LoginTicket selectByTicket(String ticket);//就这个

        @Update({"update",TABLE_NAME,"set status=#{status} where ticket=#{ticket}"})
        void updateStatus(@Param("ticket") String ticket,@Param("status") int status);
    }

