package com.mei.hui.miner.test;

import com.mei.hui.miner.MinerApplication;
import com.mei.hui.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinerApplication .class)
@Slf4j
public class MinerTest {

    @Test
    public void entry(){
        String token = AESUtil.encrypt("1");
        log.info("token = {}",token);
    }
}
