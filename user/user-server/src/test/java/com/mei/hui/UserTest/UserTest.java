package com.mei.hui.UserTest;

import com.mei.hui.config.AESUtil;
import com.mei.hui.config.JwtUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.config.smsConfig.SmsConfig;
import com.mei.hui.user.UserApplication;
import com.mei.hui.user.common.Constants;
import com.mei.hui.util.SystemConstants;
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

    @Test
    public void createToken() {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put(SystemConstants.USERID,5);
//        claims.put(SystemConstants.CURRENCYID,2L);
//        claims.put(SystemConstants.PLATFORM,Constants.WEB);
        //生成token
        String token = JwtUtil.createToken(5L,2L,Constants.WEB);
        System.out.print(token);
    }

    @Test
    public void testToken() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJkZWxfZmxhZyI6IjAiLCJ1c2VySWQiOjUsImlhdCI6MTYyMjYxNTk5MSwicGxhdGZvcm0iOiJ3ZWIiLCJzdGF0dXMiOiIwIn0.vwrfVfYueImrm55SMAmHOXhV7Z94uDfM2qLWWd8UhQM";
        Claims claims = JwtUtil.parseToken(token);

        Long userId = (Long) claims.get(SystemConstants.USERID);
        Object currency = claims.get(SystemConstants.CURRENCYID);
        Long currencyId = null;
        if(currency != null){
            currencyId = (Long) currency;
        }
        log.info("陈宫");
    }


}
