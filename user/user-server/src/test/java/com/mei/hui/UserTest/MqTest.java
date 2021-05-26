//package com.mei.hui.UserTest;
//
//import com.mei.hui.user.UserApplication;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@SpringBootTest(classes = UserApplication.class)
//@RunWith(SpringRunner.class)
//@Slf4j
//public class MqTest {
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    @Test
//    public void sendMsg(){
//        for(int i=0;i<100;i++){
//            //第一个参数为队列名称,第二个参数为要发送的消息对象,这里传的是一个字符串
//            log.info("hello rabbit {}",i);
//            rabbitTemplate.convertAndSend("test","hello rabbit"+i);
//        }
//
//    }
//}