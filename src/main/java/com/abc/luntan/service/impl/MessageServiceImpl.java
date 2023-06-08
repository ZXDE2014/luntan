package com.abc.luntan.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.abc.luntan.dto.MessageDTO;
import com.abc.luntan.dto.UserDTO;
import com.abc.luntan.entity.User;
import com.abc.luntan.service.UserService;
import com.abc.luntan.utils.BadWordUtil;
import com.abc.luntan.utils.SystemConstants;
import com.abc.luntan.utils.UserHolder;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.abc.luntan.entity.Message;
import com.abc.luntan.service.MessageService;
import com.abc.luntan.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserService userService;

    @Override
    public CommonResult sendMessage(String toId, String content) {
        String fromId = UserHolder.getUser().getEmail();
        String conversionId = "";
        if (fromId.compareTo(toId) < 0) {
            conversionId = fromId + "->" + toId;
        } else {
            conversionId = toId + "->" + fromId;
        }
        String newContent = BadWordUtil.getInstance().replaceBadWord(content);
        Message message = new Message(null, fromId, toId, conversionId, newContent, 0, DateUtil.date());
        if (save(message)) {
            return CommonResult.success(message);
        }
        return CommonResult.fail("failed to send message");
    }

    @Override
    public CommonResult withdrawMessage(Integer id) {
        Date date = DateUtil.date();
        Message message = getById(id);
        if (message == null) {
            return CommonResult.fail("没有此消息");
        }

        if(message.getStatus()==2){
            return CommonResult.fail("此消息已测回");
        }
        if(!message.getFromId().equals(UserHolder.getUser().getEmail())){
            return CommonResult.fail("不能撤回其他人的消息");
        }
        Date createdTime = message.getCreatedTime();
        Date offset = DateUtil.offset(createdTime, DateField.SECOND, 120);
        if(DateUtil.isIn(date,createdTime,offset)){
            message.setStatus(2);
            updateById(message);
            return CommonResult.success("撤回成功");
        }

        return CommonResult.fail("超过两分钟不允许撤回");
    }

    @Override
    public CommonResult getLatestNotice(String topic) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return CommonResult.success(" ");
        }
        Message message = messageMapper.selectLatestNotice(UserHolder.getUser().getEmail(), topic);
        MessageDTO messageDTO = new MessageDTO(message);
        messageDTO.setContentMap(JSONUtil.toBean(message.getContent(), HashMap.class));
        return CommonResult.success(messageDTO);
    }

    @Override
    public CommonResult getNoticeUnreadCount(String topic) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return CommonResult.success(" ");
        }
        Integer count = query().eq("status", "0").eq("from_id", "1")
                .eq("to_id", UserHolder.getUser().getEmail()).eq("conversion_id", topic).count();
        return CommonResult.success(count);
    }

    @Override
    public CommonResult getMessage(String toUserId, Integer current) {
        String myId = UserHolder.getUser().getEmail();
        String conversionId = "";
        if (myId.compareTo(toUserId) < 0) {
            conversionId = myId + "->" + toUserId;
        } else {
            conversionId = toUserId + "->" + myId;
        }
        update().eq("conversion_id", conversionId).
                eq("to_id", myId).
                eq("status",0).
                set("status",1).update();
        Page<Message> res = query().eq("conversion_id", conversionId).
                page(new Page<>(current, 5));
        User toUser = userService.getById(toUserId);
        UserDTO myUser = UserHolder.getUser();
        res.getRecords().forEach(message -> {
            this.addOtherUser(message, toUser, myUser);
        });

        return CommonResult.success(res);
    }

    private void addOtherUser(Message message, User toUser, UserDTO myUser) {
        if (message.getFromId().equals(myUser.getEmail())) {
            //消息是我发送的
            message.setFromAvatar(myUser.getAvatar());
            message.setFromNickname(myUser.getNickName());
            message.setToAvatar(toUser.getAvatar());
            message.setToNickname(toUser.getNickname());
        } else {
            message.setFromAvatar(toUser.getAvatar());
            message.setFromNickname(toUser.getNickname());
            message.setToAvatar(myUser.getAvatar());
            message.setToNickname(myUser.getNickName());
        }
    }

    @Override
    public CommonResult getFirstList(Integer current) {
        String email = UserHolder.getUser().getEmail();
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.ne("status", "2").ne("from_id", 1).and(i -> i.eq("from_id", email).or().eq("to_id", email));
        List<Message> messages = messageMapper.selectListPage((current - 1) * SystemConstants.MAX_PAGE_SIZE, SystemConstants.MAX_PAGE_SIZE, wrapper);
        return CommonResult.success(messages);
    }

    @Override
    public CommonResult getUnreadCount(String userId) {
        String fromId = UserHolder.getUser().getEmail();
        String conversionId = "";
        if (fromId.compareTo(userId) < 0) {
            conversionId = fromId + "->" + userId;
        } else {
            conversionId = userId + "->" + fromId;
        }
        Integer count = query().eq("conversion_id", conversionId).eq("to_id", userId).eq("status", 0).count();
        return  CommonResult.success(count);
    }

    @Override
    public CommonResult getAllNotice(String topic, Integer current) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return CommonResult.success(" ");
        }
        Page<Message> page = query().ne("status", "2").eq("from_id", "1").eq("to_id", UserHolder.getUser().getEmail())
                .eq("conversion_id", topic).orderByDesc("created_time").page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        update().ne("status", "2").eq("from_id", "1").eq("to_id", UserHolder.getUser().getEmail())
                .eq("conversion_id", topic).set("status", "1").update();
        return CommonResult.success(page);
    }

    @Override
    public CommonResult unreadAllMessage() {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return CommonResult.success(" ");
        }
        Integer count = query().eq("to_id", user.getEmail()).eq("status", 0).count();
        return CommonResult.success(count);
    }

    @Override
    public CommonResult unreadPrivateMessage() {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return CommonResult.success(" ");
        }
        Integer count = query().eq("to_id", user.getEmail()).eq("status", 0).ne("from_id",1).count();
        return CommonResult.success(count);
    }

    @Override
    public CommonResult unreadSystemMessage() {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return CommonResult.success(" ");
        }
        Integer count = query().eq("to_id", user.getEmail()).eq("status", 0).eq("from_id",1).count();
        return CommonResult.success(count);
    }


}




