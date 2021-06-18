package com.mei.hui.miner.test;

import com.mei.hui.config.AESUtil;
import com.mei.hui.miner.MinerApplication;
import com.mei.hui.miner.common.task.SwarmOneDayAggTask;
import com.mei.hui.miner.common.task.SwarmOneHourAggTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinerApplication.class)
@Slf4j
public class TaskTest {

    @Autowired
    private SwarmOneDayAggTask swarmOneDayAggTask;
    @Autowired
    private SwarmOneHourAggTask swarmOneHourAggTask;

    @Test
    public void swarmOneDayAggTaskTest(){
        swarmOneDayAggTask.run();
    }

    @Test
    public void swarmOneHourAggTaskTest(){
        swarmOneHourAggTask.run();
    }

}
