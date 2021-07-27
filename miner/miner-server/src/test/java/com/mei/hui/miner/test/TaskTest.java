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
import com.mei.hui.miner.entity.SysSectorInfo;
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

    /**
     * 4取模
     */
    @Test
    public void Uid(){
        String[] array = {"f0693008","f0406475","f0460078","f022522","f054370","f01016365"};

        for(int i=0;i<array.length;i++){
            String str = array[i].substring(1);
            Long intValue = Long.valueOf(str);
            long id = intValue.longValue();
            log.info("除2取模,哈希值:{}",id%2);
        }

        for(int i=0;i<array.length;i++){
            String str = array[i].substring(1);
            Long intValue = Long.valueOf(str);
            long id = intValue.longValue();
        /*    int id = Math.abs(("minerId:"+array[i]).hashCode());*/
            log.info("除3取模,哈希值:{}",id%3);
        }

        for(int i=0;i<array.length;i++){
            String str = array[i].substring(1);
            Long intValue = Long.valueOf(str);
            long id = intValue.longValue();
            log.info("除4取模,哈希值:{}",id%4);
        }

        for(int i=0;i<array.length;i++){
            String str = array[i].substring(1);
            Long intValue = Long.valueOf(str);
            long id = intValue.longValue();
            log.info("除5取模,哈希值:{}",id%5);
        }

    }


    @Autowired
    private SysSectorInfoMapper sectorInfoMapper;
    @Test
    public void findSector(){

        LambdaQueryWrapper<SysSectorInfo> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysSectorInfo::getSectorNo,52850);
        IPage<SysSectorInfo> page = sectorInfoMapper.selectPage(new Page<>(0, 10), queryWrapper);
        page.getRecords().stream().forEach(v->{
            log.info(JSON.toJSONString(v));
        });
    }

    @Autowired
    private SwarmOneDayAggService swarmOneDayAggService;

    @Test
    public void swarmOneDayAggService(){

        LambdaQueryWrapper<SwarmOneDayAgg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SwarmOneDayAgg::getId, 1916);
        IPage<SwarmOneDayAgg> page = swarmOneDayAggService.page(new Page<>(0, 10), queryWrapper);
        page.getRecords().stream().forEach(v->{
            log.info("聚合数据:"+JSON.toJSONString(v));
        });
    }
}
