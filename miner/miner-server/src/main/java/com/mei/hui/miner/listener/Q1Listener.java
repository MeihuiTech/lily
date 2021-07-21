package com.mei.hui.miner.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitListener(queues = {"test"})//从哪个队列取消息
public class Q1Listener {

    @RabbitHandler//取到消息后的处理方法
    public void receiverMsg(String msg){
        log.info("接受消息:{}",msg);
    }
}

