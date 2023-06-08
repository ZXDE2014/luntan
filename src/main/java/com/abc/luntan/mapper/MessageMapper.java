package com.abc.luntan.mapper;

import com.abc.luntan.entity.Message;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.abc.luntan.entity.Message
 */
public interface MessageMapper extends BaseMapper<Message> {

    Message selectLatestNotice(@Param("userId") String userId, @Param("topic") String topic);
    List<Message> selectListPage(@Param("offset") long offset, @Param("size") long size, @Param("ew") Wrapper wrapper);

}




