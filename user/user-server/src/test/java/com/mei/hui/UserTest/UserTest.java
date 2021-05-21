package com.mei.hui.UserTest;

import com.mei.hui.config.AESUtil;
import com.mei.hui.config.JwtUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.config.smsConfig.SmsConfig;
import com.mei.hui.user.UserApplication;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJkZWxfZmxhZyI6IjAiLCJleHAiOjE2MjEzODUyNDAsInVzZXJJZCI6MSwiaWF0IjoxNjIxMzI1MjQwLCJwbGF0Zm9ybSI6IndlYiIsInN0YXR1cyI6IjAifQ.lu6NYnOwgKX4KjL9Sh4TbqYt2X2X3ewrNOPh2RXoEFI";
        Claims claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("userId");
        String name = (String) claims.get("name");
        log.info("userId:{}，name:{}",userId,name);
    }


    /**
     * 判断字符串中是否包含中文
     * @param str
     * 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        String str = "123abd";
        log.info("是否包含中文:{}",str.length());

    }
}
