package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mei.hui.miner.entity.SysMachineInfo;
import com.mei.hui.miner.mapper.SysMachineInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@Slf4j
public class MachineDeleteTask {

    @Autowired
    private SysMachineInfoMapper sysMachineInfoMapper;
    @Value("${spring.profiles.active}")
    private String env;

    @Scheduled(cron = "0 50 23 */1 * ?")
    public void run() {
        log.info("======================MachineDeleteTask-start===================");
        if("dev".equals(env)){
            log.info("开发环境,不执行");
            return;
        }
        LambdaQueryWrapper<SysMachineInfo> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysMachineInfo::getOnline,0);
        queryWrapper.lt(SysMachineInfo::getUpdateTime, LocalDateTime.now().minusHours(24));
        List<SysMachineInfo> sysMachineInfoList = sysMachineInfoMapper.selectList(queryWrapper);
        log.info("数据条数:{}",sysMachineInfoList.size());
        List<Long> ids = sysMachineInfoList.stream().map(v -> v.getId()).collect(Collectors.toList());
        if(ids.size() > 0){
            sysMachineInfoMapper.deleteBatchIds(ids);
        }
        log.info("======================MachineDeleteTask-end===================");
    }
}
