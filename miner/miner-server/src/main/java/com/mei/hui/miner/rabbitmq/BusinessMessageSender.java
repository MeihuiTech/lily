package com.mei.hui.miner.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.mei.hui.miner.rabbitmq.RabbitMQConfig.BUSINESS_EXCHANGE_NAME;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/24 15:57
 **/
@Component
public class BusinessMessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMsg(String msg){
        rabbitTemplate.convertSendAndReceive(BUSINESS_EXCHANGE_NAME, "", msg);
    }

}
