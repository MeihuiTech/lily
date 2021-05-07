package com.mei.hui.UserTest;

import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.user.UserApplication;
import com.mei.hui.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@Slf4j
public class UserTest {

    @Autowired
    private RedisUtil redisUtil;

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

}
