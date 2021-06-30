package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mei.hui.miner.entity.SysMachineInfo;
import com.mei.hui.miner.mapper.SysMachineInfoMapper;
import com.mei.hui.miner.service.ISysMachineInfoService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class MachineTask {

    @Autowired
    private SysMachineInfoMapper sysMachineInfoMapper;
    @Value("${spring.profiles.active}")
    private String env;

    @Scheduled(cron = "0 */1 * * * ?")
    public void run() {
        log.info("======================MachineTask-start===================");
        if("dev".equals(env)){
            log.info("开发环境,不执行");
            return;
        }
        LambdaQueryWrapper<SysMachineInfo> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysMachineInfo::getOnline,1);
        List<SysMachineInfo> sysMachineInfoList = sysMachineInfoMapper.selectList(queryWrapper);
        for (SysMachineInfo sysMachineInfo : sysMachineInfoList) {
            log.info("处理矿机:{}", JSON.toJSONString(sysMachineInfo));
            long date = sysMachineInfo.getUpdateTime().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            long nowDate = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if ((nowDate - date) > (60 * 1000)) {
                SysMachineInfo machine = new SysMachineInfo();
                machine.setOnline(0);
                machine.setId(sysMachineInfo.getId());
                int row = sysMachineInfoMapper.updateSysMachineInfo(machine);
                log.info("更新结果:{}",row);
            }
        }
        log.info("======================MachineTask-end===================");
    }
}
