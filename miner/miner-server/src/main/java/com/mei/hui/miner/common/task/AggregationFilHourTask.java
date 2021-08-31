package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.mei.hui.miner.entity.SysAggPowerHour;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.service.ISysAggPowerHourService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.CurrencyEnum;
import com.mei.hui.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * fil币账户按小时聚合定时任务
 */
@Configuration
@EnableScheduling
public class AggregationFilHourTask {
    private static final Logger log = LoggerFactory.getLogger(AggregationFilHourTask.class);


    @Autowired
    private ISysAggPowerHourService sysAggPowerHourService;

    @Autowired
    private ISysMinerInfoService sysMinerInfoService;

    @Value("${spring.profiles.active}")
    private String env;

    /**
     * fil币账户按小时聚合，每小时59分执行
     */
    @Scheduled(cron = "0 59 */1 * * ?")
//    @Scheduled(cron = "* */3 * * * ?")
    public void dailyAccount() {
        log.info("======================fil币AggregationFilHourTask-start===================");
        if("dev".equals(env)){
            log.info("开发环境,不执行");
            return;
        }
        List<SysMinerInfo> list = sysMinerInfoService.list();
        log.info("获取矿工列表,出参: {}", JSON.toJSONString(list));
        for (SysMinerInfo info : list) {
            log.info("矿工信息列表:{}",JSON.toJSONString(info));
            insertPower(info);
        }
        log.info("======================fil币AggregationFilHourTask-end===================");
    }

    /**
     * 算力按小时聚合表
     * @param info
     */
    private void insertPower(SysMinerInfo info){
        log.info("算力按小时聚合表");
        LocalDateTime date = DateUtils.lDTBeforeLocalDateTimeHourDate();
        String minerId = info.getMinerId();
        log.info("查询当前小时：根据minerId、date查询算力按小时聚合表list,入参:minerId = {},date={}",minerId,date);
        List<SysAggPowerHour> dbSysAggPowerHourList = sysAggPowerHourService.selectSysAggPowerHourByMinerIdDate(CurrencyEnum.FIL.name(),minerId, date);
        log.info("查询当前小时：根据minerId、date查询算力按小时聚合表list,出参:{}",JSON.toJSONString(dbSysAggPowerHourList));
        if (dbSysAggPowerHourList != null && dbSysAggPowerHourList.size() > 0) {
            log.info("算力按小时聚合表中已经存在，不插入跳过,入参:minerId = {},date={}",minerId,date);
            return;
        }

        String startDate = DateUtils.lDTYesterdayBeforeLocalDateTimeHour();
        String endDate = DateUtils.lDTBeforeBeforeLocalDateTimeHour();
        log.info("查询近24小时内上一个小时：根据minerId、date查询算力按小时聚合表list,入参:minerId =【{}】,startDate=【{}】,endDate=【{}】",minerId,startDate,endDate);
        List<SysAggPowerHour> beforeSysAggPowerHourList = sysAggPowerHourService.selectLastSysAggPowerHourByMinerIdDate(CurrencyEnum.FIL.name(),minerId,startDate,endDate);
        log.info("查询近24小时内上一个小时：根据minerId、date查询算力按小时聚合表list,出参:{}",JSON.toJSONString(beforeSysAggPowerHourList));

        SysAggPowerHour sysAggPowerHour = new SysAggPowerHour();
        sysAggPowerHour.setMinerId(minerId);
        sysAggPowerHour.setDate(date);
        sysAggPowerHour.setPowerAvailable(info.getPowerAvailable());
        sysAggPowerHour.setTotalBlockAward(info.getTotalBlockAward());
        sysAggPowerHour.setTotalBlocks(info.getTotalBlocks());
        if (beforeSysAggPowerHourList != null && beforeSysAggPowerHourList.size() > 0) {
            sysAggPowerHour.setPowerIncrease(info.getPowerAvailable().subtract(beforeSysAggPowerHourList.get(0).getPowerAvailable()));
            sysAggPowerHour.setBlockAwardIncrease(info.getTotalBlockAward().subtract(beforeSysAggPowerHourList.get(0).getTotalBlockAward()));
            sysAggPowerHour.setBlocksPerDay(info.getTotalBlocks() - beforeSysAggPowerHourList.get(0).getTotalBlocks());
        } else {
            sysAggPowerHour.setPowerIncrease(BigDecimal.ZERO);
            sysAggPowerHour.setBlockAwardIncrease(BigDecimal.ZERO);
            sysAggPowerHour.setBlocksPerDay(0L);
        }
        sysAggPowerHour.setType(CurrencyEnum.FIL.name());
        sysAggPowerHour.setCreateTime(LocalDateTime.now());
        log.info("算力按小时聚合表插入数据,入参:{}",JSON.toJSONString(sysAggPowerHour));
        Boolean flag = sysAggPowerHourService.save(sysAggPowerHour);
        log.info("算力按小时聚合表插入数据,返回值:{}",flag);

    }



}
