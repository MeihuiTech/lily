package com.mei.hui.UserTest;

import com.mei.hui.config.HttpUtil;
import com.mei.hui.config.smsConfig.SmsUtil;
import com.mei.hui.user.UserApplication;
import com.mei.hui.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@Slf4j
public class UserTest {


    @Autowired
    private SmsUtil smsUtil;
    @Test
    public void testToken() {
        int min = 123456;
        int max = 999999;
        Random r = new Random();
        String code = String.valueOf(r.nextInt(max - min + 1) + min);
        smsUtil.send("18310536874",code);


    }




}
