package com.mei.hui.miner.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/28 14:55
 **/
public class DeadSender {

    private static final String EXCHANGE_NAME = "amqp.car";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.10.15.8");
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明一个名为amqp.car的交换机，将消息发送至该交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        //每隔一秒发送一条消息，设置routind Key 为 car
        while (true) {
            channel.basicPublish(EXCHANGE_NAME, "car", null, "我是死信消息".getBytes("UTF-8"));
            TimeUnit.SECONDS.sleep(1);
        }
    }

}
