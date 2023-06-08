package com.abc.luntan.Event;



import cn.hutool.json.JSONUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.abc.luntan.utils.RabbitConstants.TOPIC_EXCHANGE;

@Component
public class EventProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 直接发送
    public void fireEventDirect(Event event) {
        // 将事件发布到指定的主题(评论、点赞、关注)
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE,event.getRoutingKey(), JSONUtil.toJsonStr(event));
    }

}
