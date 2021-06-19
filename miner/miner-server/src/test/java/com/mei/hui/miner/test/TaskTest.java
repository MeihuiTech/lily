package com.mei.hui.miner.test;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.AESUtil;
import com.mei.hui.miner.MinerApplication;
import com.mei.hui.miner.common.task.SwarmOneDayAggTask;
import com.mei.hui.miner.common.task.SwarmOneHourAggTask;
import com.mei.hui.miner.feign.vo.NodePageListVO;
import com.mei.hui.miner.mapper.SwarmAggMapper;
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
    @Autowired
    private SwarmAggMapper swarmAggMapper;
    @Test
    public void swarmOneDayAggTaskTest(){
        swarmOneDayAggTask.run();
    }

    @Test
    public void swarmOneHourAggTaskTest(){
        swarmOneHourAggTask.run();
    }

    @Test
    public void swarmAggMapperTest(){

        QueryWrapper<NodePageListVO> queryWrapper = new QueryWrapper();

        queryWrapper.orderByAsc("yestodayTicketAvail");
        IPage<NodePageListVO> page = swarmAggMapper.findNodePageList(new Page<>(0, 2), queryWrapper);
        log.info("结果:{}", JSON.toJSONString(page.getRecords()));
    }

}
