package com.mei.hui.miner.test;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.HttpUtil;
import com.mei.hui.miner.MinerApplication;
import com.mei.hui.miner.common.task.UpdateNodeAddressTask;
import com.mei.hui.miner.entity.SwarmOneDayAgg;
import com.mei.hui.miner.entity.SysMachineInfo;
import com.mei.hui.miner.entity.SysSectorInfo;
import com.mei.hui.miner.mapper.SysMachineInfoMapper;
import com.mei.hui.miner.mapper.SysSectorInfoMapper;
import com.mei.hui.miner.service.SwarmOneDayAggService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinerApplication.class)
@Slf4j
public class TaskTest {


 /*   @Test
    public void initSectorToRedis(){
        HttpUtil.doPost("http://10.10.15.2:8082/fil/reported/initSectorToRedis","");
    }


    @Test
    public void initSectorDuration(){
        HttpUtil.doPost("http://10.10.15.2:8082/fil/reported/initSectorDuration","");
    }*/

/*    @Test
    public void test(){
        String result = HttpUtil.doGet("http://ip-api.com/json/45.146.57.83?lang=zh-CN", "");
        log.info(result);
    }*/

    @Test
    public void test(){
        HttpUtil.doPost("http://10.10.15.1:8082/fil/reported/initMinerIp","");
    }




    @Autowired
    private SysMachineInfoMapper sysMachineInfoMapper;

    @Test
    public void swarmOneDayAggService(){
        SysMachineInfo vo = sysMachineInfoMapper.selectSysMachineInfoByMinerAndHostname("f01016365", "kminer7a-10-10-11-71");
        log.info("结果:{}",JSON.toJSONString(vo));
        
    }
}
