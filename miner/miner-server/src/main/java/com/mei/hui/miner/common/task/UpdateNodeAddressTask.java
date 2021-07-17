package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.HttpUtil;
import com.mei.hui.miner.entity.MinerLongitudeLatitude;
import com.mei.hui.miner.service.IMinerLongitudeLatitudeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class UpdateNodeAddressTask {

    @Value("${spring.profiles.active}")
    private String env;

    @Autowired
    private IMinerLongitudeLatitudeService minerLongitudeLatitudeService;

    /*每天早晨6点执行*/
    @Scheduled(cron = "0 0 6 * * ?")
    public void run() {
        log.info("======================UpdateNodeAddressTask-start===================");
        if("dev".equals(env)){
            log.info("开发环境,不执行");
            return;
        }
        minerLongitudeLatitudeService.initMinerIp();
        log.info("======================UpdateNodeAddressTask-end===================");
    }
}
