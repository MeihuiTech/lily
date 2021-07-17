package com.mei.hui.miner.test;

import com.mei.hui.config.HttpUtil;
import com.mei.hui.miner.MinerApplication;
import com.mei.hui.miner.common.task.UpdateNodeAddressTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinerApplication.class)
@Slf4j
public class TaskTest {

    @Autowired
    private UpdateNodeAddressTask updateNodeAddressTask;

    @Test
    public void swarmAggMapperTest(){

        updateNodeAddressTask.run();
    }


}
