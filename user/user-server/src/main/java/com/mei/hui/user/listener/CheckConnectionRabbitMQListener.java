package com.mei.hui.user.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CheckConnectionRabbitMQListener {


    /**
     * 宕机运维报警功能消费消息mq
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitListener(queues = {"check.connection.status.queue"})
    @RabbitHandler
    public void reportFilBlockAwardMq(Channel channel, Message message) throws IOException {
        byte[] body = message.getBody();
        String messageStr = new String(body,"UTF-8");
        log.info("宕机运维报警功能确认消息mq入参【{}】：" , messageStr);
        // 对于每个Channel来说，每个消息都会有一个DeliveryTag，一般用接收消息的顺序(index)来表示，一条消息就为1
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


}

