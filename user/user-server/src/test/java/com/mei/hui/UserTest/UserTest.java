package com.mei.hui.UserTest;

import com.mei.hui.config.HttpUtil;
import com.mei.hui.user.UserApplication;
import com.mei.hui.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@Slf4j
public class UserTest {


    @Test
    public void testToken() {
        HttpUtil.doGet("http://ip-api.com/json/24.48.0.1?lang=zh-CN","");


    }




}
