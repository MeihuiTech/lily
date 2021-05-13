package com.mei.hui.UserTest;

import com.mei.hui.config.JwtUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.config.smsConfig.SmsConfig;
import com.mei.hui.user.UserApplication;
import com.mei.hui.util.AESUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@Slf4j
public class UserTest {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SmsConfig smsConfig;

    @Value("${swagger.is.enable}")
    private String sdd;

    @Test
    public void swaggerTest(){

        log.info("swagger.is.enable={}",sdd);
    }

    @Test
    public void entry(){
        String token = AESUtil.encrypt("admin123");
        log.info("token = {}",token);
    }

    /**
     * 测试本地是否能连上redis服务器
     */
    @Test
    public void testRedis() {
        redisUtil.set("testRedisKey","testRedisValue");
        System.out.print(redisUtil.get("testRedisKey"));
    }

    @Test
    public void getIP() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        log.info("IP:"+address.getHostAddress());
    }

    @Test
    public void configTest(){
        log.info("sms url:"+smsConfig.getUrl());
    }

    /**
     * jwt 测试
     */
    @Test
    public void jwtEncodeTest(){
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId",33);
        claims.put("name","鲍红建");
        String token = JwtUtil.createToken(claims);
        log.info("token:{}",token);
    }

    @Test
    public void jwtDecodeTest(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoi6bKN57qi5bu6IiwiZXhwIjoxNjIwNzE0MjY3LCJ1c2VySWQiOjMzfQ.VV4FZq21N7ZIStCmLfG8iV35_NZubc1JOaJ4eaIA";
        Claims claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("userId");
        String name = (String) claims.get("name");
        log.info("userId:{}，name:{}",userId,name);
    }

}
