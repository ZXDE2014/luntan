package com.abc.luntan.service;

import com.abc.luntan.entity.Message;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface MessageService extends IService<Message> {

    CommonResult sendMessage(String toId, String content);

    CommonResult withdrawMessage(Integer id);

    CommonResult unreadAllMessage();

    CommonResult unreadPrivateMessage();

    CommonResult unreadSystemMessage();

    CommonResult getLatestNotice(String topic);

    CommonResult getNoticeUnreadCount(String topic);

    CommonResult getAllNotice(String topic, Integer current);

    CommonResult getMessage(String userId, Integer current);

    CommonResult getFirstList(Integer current);

    CommonResult getUnreadCount(String userId);
}
