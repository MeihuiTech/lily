package com.mei.hui.miner.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/28 15:00
 **/
public class DeadReceive {


    private static final String EXCHANGE_NAME = "amqp.car";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.10.15.8");
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        //声明一个队列用于接收消息
        String queueName = "car.queue";
        channel.queueDeclare(queueName, true, false, false, null);
        //绑定队列，设置routing key为car
        channel.queueBind(queueName, EXCHANGE_NAME, "car");
        //消息接收后的回调方法
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" 收到信息：" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            //收到消息后直接拒绝，并设置requeue属性为false，这样被拒绝的消息就不会重新回到原始队列中而是转发到死信交换机
            channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
//            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        //接收消息，关闭自动ack
        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });
    }

}
