package com.abc.luntan.config;

import com.abc.luntan.utils.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.abc.luntan.utils.RabbitConstants.*;

@Configuration
public class RabbitMqConfig {

    //创建一个名为comment的队列
    @Bean
    public Queue commentQueue(){
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，有消息者订阅本队列，然后所有消费者都解除订阅此队列，会自动删除。
        // arguments：队列携带的参数，比如设置队列的死信队列，消息的过期时间等等。
        return new Queue(TOPIC_COMMENT,true);
    }
    @Bean
    public Queue likeQueue(){
        return new Queue(TOPIC_LIKE,true);
    }
    @Bean
    public Queue followQueue(){
        return new Queue(TOPIC_FOLLOW,true);
    }


    @Bean
    DirectExchange topicDirect(){
        return (DirectExchange) ExchangeBuilder.
                directExchange(TOPIC_EXCHANGE).
                durable(true).
                build();
    }

    /**
     * 将多个队列绑定到topic交换机
     */
    @Bean
    Binding orderBinding1(DirectExchange topicDirect, Queue commentQueue){
        return BindingBuilder
                .bind(commentQueue)
                .to(topicDirect)
                .with(TOPIC_COMMENT)
                ;
    }
    @Bean
    Binding orderBinding2(DirectExchange topicDirect, Queue likeQueue){
        return BindingBuilder
                .bind(likeQueue)
                .to(topicDirect)
                .with(TOPIC_LIKE)
                ;
    }
    @Bean
    Binding orderBinding3(DirectExchange topicDirect, Queue followQueue){
        return BindingBuilder
                .bind(followQueue)
                .to(topicDirect)
                .with(TOPIC_FOLLOW)
                ;
    }


    //订单交换机：死信交换机
    @Bean
    DirectExchange orderDirect(){
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }
    //订单延迟取消交换机
    @Bean
    DirectExchange orderTtlDirect(){
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }
    //订单队列
    @Bean
    public Queue orderQueue() {
        return new Queue(QueueEnum.QUEUE_ORDER_CANCEL.getName());
    }

    //订单延迟取消队列
    @Bean
    public Queue orderTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_ORDER_CANCEL.getExchange())     //到期后转发的死信交换机
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey())  //到期后转发的死信路由键
                .build();

    }

    /**
     * 将订单队列绑定到交换机
     */
    @Bean
    Binding orderBinding(DirectExchange orderDirect,Queue orderQueue){
        return BindingBuilder
                .bind(orderQueue)
                .to(orderDirect)
                .with(QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey())
                ;
    }
    /**
     * 将订单延迟队列绑定到交换机
     */
    @Bean
    Binding orderTtlBinding(DirectExchange orderTtlDirect,Queue orderTtlQueue){
        return BindingBuilder
                .bind(orderTtlQueue)
                .to(orderTtlDirect)
                .with(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey());
    }
}
