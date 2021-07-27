package com.mei.hui.miner.rabbitmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.mei.hui.miner.rabbitmq.RabbitMQConfig.DEAD_LETTER_QUEUEA_NAME;
import static com.mei.hui.miner.rabbitmq.RabbitMQConfig.DEAD_LETTER_QUEUEB_NAME;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/24 15:57
 **/
@Component
public class DeadLetterMessageReceiver {

    @RabbitListener(queues = DEAD_LETTER_QUEUEA_NAME)
    public void receiveA(Message message, Channel channel) throws IOException {
        System.out.println("收到死信消息A：" + new String(message.getBody()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = DEAD_LETTER_QUEUEB_NAME)
    public void receiveB(Message message, Channel channel) throws IOException {
        System.out.println("收到死信消息B：" + new String(message.getBody()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
