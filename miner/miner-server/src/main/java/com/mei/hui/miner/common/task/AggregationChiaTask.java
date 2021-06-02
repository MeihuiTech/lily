package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.mei.hui.miner.common.enums.CurrencyEnum;
import com.mei.hui.miner.entity.ChiaMiner;
import com.mei.hui.miner.entity.SysAggAccountDaily;
import com.mei.hui.miner.entity.SysAggPowerDaily;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.service.IChiaMinerService;
import com.mei.hui.miner.service.ISysAggAccountDailyService;
import com.mei.hui.miner.service.ISysAggPowerDailyService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * chia币定时任务
 */
@Configuration
@EnableScheduling
public class AggregationChiaTask {
    private static final Logger log = LoggerFactory.getLogger(AggregationChiaTask.class);

    @Autowired
    private ISysAggAccountDailyService sysAggAccountDailyService;

    @Autowired
    private ISysAggPowerDailyService sysAggPowerDailyService;

    @Autowired
    private IChiaMinerService chiaMinerService;



    /**
     * chia币账户按天聚合
     */
    //或直接指定时间间隔，例如：5秒
    @Scheduled(cron = "0 0 0 */1 * ?")
    public void chiaDailyAccount() {
        log.info("======================chia币AggregationTask-start===================");
        ChiaMiner chiaMiner = new ChiaMiner();
        int pageNum = 1;
        int pageSize = 100;
        while (true) {
            // 每100条插入一次
            PageHelper.startPage(pageNum,pageSize, "id");
            log.info("获取旷工,入参: pageNum = {},pageSize = {}",pageNum,pageSize);
            List<ChiaMiner> list = chiaMinerService.findChiaMinerList(chiaMiner);
            log.info("获取旷工,出参: {}", JSON.toJSONString(list));
            for (ChiaMiner dbchiaMiner : list) {
                log.info("旷工信息:{}",JSON.toJSONString(dbchiaMiner));
                insertSysAggAccountDaily(dbchiaMiner);
                insertSysAggPowerDaily(dbchiaMiner);
            }
            if (list.size() < pageSize) {
                break;
            } else {
                pageNum ++;
            }
        }
        log.info("======================chia币AggregationTask-end===================");
    }


    /**
     * 算力按天聚合表-chia币
     * @param chiaMiner
     */
    private void insertSysAggPowerDaily(ChiaMiner chiaMiner){
        log.info("算力按天聚合表");
        String date = DateUtils.getDate();
        //当前日期转换成YYYY-mm-dd 格式
        String dateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, DateUtils.parseDate(date));
        log.info("查询当天算力聚合表,入参:minerId = {},date={}",chiaMiner.getMinerId(),dateStr);
        SysAggPowerDaily today = sysAggPowerDailyService.selectSysAggPowerDailyByMinerIdAndDate(chiaMiner.getMinerId(),dateStr);
        log.info("查询当天算力聚合表,出参:{}",JSON.toJSONString(today));
        if (today == null) {
            String yesterDateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, DateUtils.addDays(DateUtils.parseDate(date), -1));
            log.info("查询昨天算力聚合表,入参:minerId ={},date={}",chiaMiner.getMinerId(),yesterDateStr);
            SysAggPowerDaily yesterday = sysAggPowerDailyService.selectSysAggPowerDailyByMinerIdAndDate(chiaMiner.getMinerId(),yesterDateStr);
            log.info("查询昨天算力聚合表,出参:{}",JSON.toJSONString(yesterDateStr));
            SysAggPowerDaily sysAggPowerDaily = new SysAggPowerDaily();
            sysAggPowerDaily.setMinerId(chiaMiner.getMinerId());
            sysAggPowerDaily.setDate(date);
            sysAggPowerDaily.setPowerAvailable(chiaMiner.getPowerAvailable());
            if (yesterday != null) {
                sysAggPowerDaily.setPowerIncrease(chiaMiner.getPowerAvailable().subtract(yesterday.getPowerAvailable()));
                sysAggPowerDaily.setBlockAwardIncrease(chiaMiner.getTotalBlockAward().subtract(yesterday.getTotalBlockAward()));
                sysAggPowerDaily.setTotalBlocks(chiaMiner.getTotalBlocks() - yesterday.getTotalBlocks());
            } else {
                sysAggPowerDaily.setPowerIncrease(chiaMiner.getPowerAvailable());
                sysAggPowerDaily.setBlockAwardIncrease(chiaMiner.getTotalBlockAward());
            }
            sysAggPowerDaily.setTotalBlockAward(chiaMiner.getTotalBlockAward());
            sysAggPowerDaily.setType(CurrencyEnum.CHIA.name());
            log.info("算力聚合表插入数据,入参:{}",JSON.toJSONString(sysAggPowerDaily));
            int result = sysAggPowerDailyService.insertSysAggPowerDaily(sysAggPowerDaily);
            log.info("算力聚合表插入数据,返回值:{}",result);
        }
    }

    /**
     * 账户按天聚合表-chia币
     * @param chiaMiner
     */
    private void insertSysAggAccountDaily(ChiaMiner chiaMiner) {
        log.info("账户按天聚合表");
        String date = DateUtils.getDate();
        log.info("查询账户聚合表,入参:minerId = {},date={}",chiaMiner.getMinerId(),date);
        SysAggAccountDaily data = sysAggAccountDailyService.selectSysAggAccountDailyByMinerIdAndDate(chiaMiner.getMinerId(),date);
        log.info("查询账户聚合表,出参:",JSON.toJSONString(data));
        if (data == null) {
            SysAggAccountDaily sysAggAccountDaily = new SysAggAccountDaily();
            sysAggAccountDaily.setMinerId(chiaMiner.getMinerId());
            sysAggAccountDaily.setDate(date);
            sysAggAccountDaily.setBalanceAccount(chiaMiner.getBalanceMinerAccount());
            sysAggAccountDaily.setType(CurrencyEnum.CHIA.name());
            log.info("账户聚合表新增数据,入参:{}",JSON.toJSONString(sysAggAccountDaily));
            int result = sysAggAccountDailyService.insertSysAggAccountDaily(sysAggAccountDaily);
            log.info("账户聚合表新增数据,返回值:{}",result);
        }
    }



}
