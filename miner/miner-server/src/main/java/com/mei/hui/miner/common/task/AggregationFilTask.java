package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.mei.hui.miner.entity.SysAggAccountDaily;
import com.mei.hui.miner.entity.SysAggPowerDaily;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.service.ISysAggAccountDailyService;
import com.mei.hui.miner.service.ISysAggPowerDailyService;
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

import java.util.List;

/**
 * fil币定时任务
 */
@Configuration
@EnableScheduling
public class AggregationFilTask {
    private static final Logger log = LoggerFactory.getLogger(AggregationFilTask.class);

    @Autowired
    private ISysAggAccountDailyService sysAggAccountDailyService;

    @Autowired
    private ISysAggPowerDailyService sysAggPowerDailyService;

    @Autowired
    private ISysMinerInfoService sysMinerInfoService;

    @Value("${spring.profiles.active}")
    private String env;

    /**
     * fil币账户按天聚合，每天晚上23点59分55秒执行
     */
    @Scheduled(cron = "55 59 23 */1 * ?")
    public void dailyAccount() {
        log.info("======================fil币AggregationTask-start===================");
        if("dev".equals(env)){
            log.info("开发环境,不执行");
            return;
        }

        SysMinerInfo sysMinerInfo = new SysMinerInfo();
        int pageNum = 1;
        int pageSize = 100;
        while (true) {
            PageHelper.startPage(pageNum,pageSize, "id");
            log.info("获取旷工,入参: pageNum = {},pageSize = {}",pageNum,pageSize);
            List<SysMinerInfo> list = sysMinerInfoService.findMinerInfoList(sysMinerInfo);
            log.info("获取旷工,出参: {}", JSON.toJSONString(list));
            for (SysMinerInfo info : list) {
                log.info("旷工信息:{}",JSON.toJSONString(info));
                insertAccount(info);
                insertPower(info);
            }
            if (list.size() < pageSize) {
                break;
            } else {
                pageNum ++;
            }
        }
        log.info("======================fil币AggregationTask-end===================");
    }

    /**
     * 算力按天聚合表
     * @param info
     */
    private void insertPower(SysMinerInfo info){
        log.info("算力按天聚合表");
        String date = DateUtils.getDate();
        //当前日期转换成YYYY-mm-dd 格式
        String dateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, DateUtils.parseDate(date));
        log.info("查询当天算力聚合表,入参:minerId = {},date={}",info.getMinerId(),dateStr);
        SysAggPowerDaily today = sysAggPowerDailyService.selectSysAggPowerDailyByMinerIdAndDate(info.getMinerId(),dateStr);
        log.info("查询当天算力聚合表,出参:{}",JSON.toJSONString(today));
        if (today == null) {
            String yesterDateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, DateUtils.addDays(DateUtils.parseDate(date), -1));
            log.info("查询昨天算力聚合表,入参:minerId ={},date={}",info.getMinerId(),yesterDateStr);
            SysAggPowerDaily yesterday = sysAggPowerDailyService.selectSysAggPowerDailyByMinerIdAndDate(info.getMinerId(),yesterDateStr);
            log.info("查询昨天算力聚合表,出参:{}",JSON.toJSONString(yesterday));
            SysAggPowerDaily sysAggPowerDaily = new SysAggPowerDaily();
            sysAggPowerDaily.setMinerId(info.getMinerId());
            sysAggPowerDaily.setDate(date);
            sysAggPowerDaily.setPowerAvailable(info.getPowerAvailable());
            sysAggPowerDaily.setTotalBlocks(info.getTotalBlocks());
            if (yesterday != null) {
                sysAggPowerDaily.setPowerIncrease(info.getPowerAvailable().subtract(yesterday.getPowerAvailable()));
                sysAggPowerDaily.setBlocksPerDay(info.getTotalBlocks() - yesterday.getTotalBlocks());
            } else {
                sysAggPowerDaily.setPowerIncrease(info.getPowerAvailable());
                sysAggPowerDaily.setBlocksPerDay(info.getTotalBlocks());
            }
            sysAggPowerDaily.setTotalBlockAward(info.getTotalBlockAward());
            if (yesterday != null) {
                sysAggPowerDaily.setBlockAwardIncrease(info.getTotalBlockAward().subtract(yesterday.getTotalBlockAward()));
            } else {
                sysAggPowerDaily.setBlockAwardIncrease(info.getTotalBlockAward());
            }
            sysAggPowerDaily.setType(CurrencyEnum.FIL.name());
            log.info("算力聚合表插入数据,入参:{}",JSON.toJSONString(sysAggPowerDaily));
            int result = sysAggPowerDailyService.insertSysAggPowerDaily(sysAggPowerDaily);
            log.info("算力聚合表插入数据,返回值:{}",result);
        }
    }

    /**
     * 账户按天聚合表
     * @param info
     */
    private void insertAccount(SysMinerInfo info) {
        log.info("账户按天聚合表");
        String date = DateUtils.getDate();
        log.info("查询账户聚合表,入参:minerId = {},date={}",info.getMinerId(),date);
        SysAggAccountDaily data = sysAggAccountDailyService.selectSysAggAccountDailyByMinerIdAndDate(info.getMinerId(),date);
        log.info("查询账户聚合表,出参:",JSON.toJSONString(data));
        if (data == null) {
            SysAggAccountDaily sysAggAccountDaily = new SysAggAccountDaily();
            sysAggAccountDaily.setMinerId(info.getMinerId());
            sysAggAccountDaily.setDate(date);
            sysAggAccountDaily.setBalanceAccount(info.getBalanceMinerAccount());
            sysAggAccountDaily.setBalanceAvailable(info.getBalanceMinerAvailable());
            sysAggAccountDaily.setSectorPledge(info.getSectorPledge());
            sysAggAccountDaily.setLockAward(info.getLockAward());
            log.info("账户聚合表新增数据,入参:{}",JSON.toJSONString(sysAggAccountDaily));
            int result = sysAggAccountDailyService.insertSysAggAccountDaily(sysAggAccountDaily);
            log.info("账户聚合表新增数据,返回值:{}",result);
        }
    }



}
