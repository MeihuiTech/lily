package com.mei.hui.miner.test;

import com.mei.hui.miner.MinerApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.print.Book;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = MinerApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class MqTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendMsg(){
        String msg = "hello rabbit";
            //第一个参数为队列名称,第二个参数为要发送的消息对象,这里传的是一个字符串
//            rabbitTemplate.convertAndSend("fil.bill.queue",msg);
            rabbitTemplate.convertAndSend("fil.reward.queue",msg);
            log.info("发送消息:{}",msg);

    }


    @Test
    public void receiveMsg(){
        // 接收来自指定队列的消息，并设置超时时间
        Message msg = rabbitTemplate.receive("fil.bill.queue",2000l);
//        String msg = (String) rabbitTemplate.receiveAndConvert("test",2000l);
        System.out.println("-------------"+msg);
    }



    /*
     * 1、单播（点对点）
     * */

    @Test
    public void contextLoads() {
        //message需要自己构造一个；定义消息体内容和消息头
        //rabbitTemplate.send(exchange,routeKey,message);

        //默认是object是消息体，只需要传入要发送的对象，自动序列化，然后保存发送给rabbitMQ
        //rabbitTemplate.convertAndSend(exchange,routeKey,object);
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "这是第一个消息");
        map.put("data", Arrays.asList("helloworldworld", 123, true));
        //对象被默认序列化以后发送出去，这是发送点对点消息
        rabbitTemplate.convertAndSend("fil.bill.queue",map);
//        rabbitTemplate.convertAndSend("exchange.direct", "atguigu.news", new Book("西游记", "吴承恩"));
    }

    //接收数据，如何将数据自动的转为json发送出去
    @Test
    public void receive() {
        Object o = rabbitTemplate.receiveAndConvert("fil.bill.queue");
        System.out.println("-------------"+o.getClass());
        System.out.println("-------------"+o);
    }


}