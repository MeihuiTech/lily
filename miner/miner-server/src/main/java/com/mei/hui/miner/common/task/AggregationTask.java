package com.mei.hui.miner.common.task;

import com.github.pagehelper.PageHelper;
import com.mei.hui.miner.entity.SysAggAccountDaily;
import com.mei.hui.miner.entity.SysAggPowerDaily;
import com.mei.hui.miner.entity.SysMinerInfo;
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

import java.time.LocalDateTime;
import java.util.List;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class AggregationTask {
    private static final Logger log = LoggerFactory.getLogger(AggregationTask.class);

    @Autowired
    private ISysAggAccountDailyService sysAggAccountDailyService;

    @Autowired
    private ISysAggPowerDailyService sysAggPowerDailyService;

    @Autowired
    private ISysMinerInfoService sysMinerInfoService;
    /**
     * 账户按天聚合
     */
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    @Scheduled(cron = "0 0 0 * * ? *")
    public void dailyAccount() {
        log.info("dailyAccount start...");
        SysMinerInfo sysMinerInfo = new SysMinerInfo();
        int pageNum = 1;
        int pageSize = 100;
        while (true) {
            PageHelper.startPage(pageNum,pageSize, "id");
            List<SysMinerInfo> list = sysMinerInfoService.findMinerInfoList(sysMinerInfo);
            for (SysMinerInfo info : list) {
                insertAccount(info);
                insertPower(info);
            }
            if (list.size() < pageSize) {
                break;
            } else {
                pageNum ++;
            }
        }
        log.info("dailyAccount end...");
    }

    private void insertPower(SysMinerInfo info){
        String date = DateUtils.getDate();
        SysAggPowerDaily today = sysAggPowerDailyService.selectSysAggPowerDailyByMinerIdAndDate(info.getMinerId(),date);
        if (today == null) {
            SysAggPowerDaily yesterday = sysAggPowerDailyService.selectSysAggPowerDailyByMinerIdAndDate(info.getMinerId(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,  DateUtils.addDays(DateUtils.parseDate(date), -1)));
            SysAggPowerDaily sysAggPowerDaily = new SysAggPowerDaily();
            sysAggPowerDaily.setMinerId(info.getMinerId());
            sysAggPowerDaily.setDate(date);
            sysAggPowerDaily.setPowerAvailable(info.getPowerAvailable().longValue());
            if (yesterday != null) {
                sysAggPowerDaily.setPowerIncrease(info.getPowerAvailable().longValue() - yesterday.getPowerAvailable());
            } else {
                sysAggPowerDaily.setPowerIncrease(info.getPowerAvailable().longValue());
            }
            sysAggPowerDailyService.insertSysAggPowerDaily(sysAggPowerDaily);
        }
    }
    private void insertAccount(SysMinerInfo info) {
        String date = DateUtils.getDate();
        SysAggAccountDaily data = sysAggAccountDailyService.selectSysAggAccountDailyByMinerIdAndDate(info.getMinerId(),date);
        if (data == null) {
            SysAggAccountDaily sysAggAccountDaily = new SysAggAccountDaily();
            sysAggAccountDaily.setMinerId(info.getMinerId());
            sysAggAccountDaily.setDate(date);
            sysAggAccountDaily.setBalanceAccount(info.getBalanceMinerAccount());
            sysAggAccountDaily.setBalanceAvailable(info.getBalanceMinerAvailable());
            sysAggAccountDaily.setSectorPledge(info.getSectorPledge());
            sysAggAccountDaily.setLockAward(info.getLockAward());
            sysAggAccountDailyService.insertSysAggAccountDaily(sysAggAccountDaily);
        }
    }
}
