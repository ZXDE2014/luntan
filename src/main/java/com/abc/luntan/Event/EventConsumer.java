package com.abc.luntan.Event;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.abc.luntan.entity.Message;
import com.abc.luntan.mapper.MessageMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.abc.luntan.utils.RabbitConstants.*;

@Slf4j
@Component
public class EventConsumer {
    @Autowired
    private MessageMapper messageMapper;


    @RabbitListener(queues = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleMessage(String msg,org.springframework.amqp.core.Message m, Channel channel){
        try {
            if(StrUtil.isBlank(msg)){
                log.error("消息的内容未空");
                return;
            }
            Event event = JSONUtil.toBean(msg, Event.class);
            if(event == null){
                log.error("消息格式有误！");
            }
            Message message = new Message();
            message.setFromId(SYSTEM_USER_ID);
            message.setToId(event.getEntityUserId());
            message.setConversionId(event.getRoutingKey());
            message.setStatus(0);
            message.setCreatedTime(DateUtil.date());
            Map<String, Object> content = new HashMap<>();
            content.put("userId", event.getUserId());
            content.put("entityType", event.getEntityType());
            content.put("entityId", event.getEntityId());
            message.setContent(JSONUtil.toJsonStr(content));
            messageMapper.insert(message);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }

    }
}
