package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.mei.hui.miner.entity.FilBaselinePowerHourAgg;
import com.mei.hui.miner.entity.FilReportNetworkData;
import com.mei.hui.miner.service.FilBaselinePowerHourAggService;
import com.mei.hui.miner.service.FilReportNetworkDataService;
import com.mei.hui.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 按天聚合Fil 基线和 全网算力
 */
@Configuration
@EnableScheduling
@Slf4j
public class FilBaselinePowerHourAggTask {

    @Autowired
    private FilReportNetworkDataService reportNetworkDataService;
    @Autowired
    private FilBaselinePowerHourAggService baselinePowerDayAggService;
    @Value("${spring.profiles.active}")
    private String env;

    /**
     * 按天聚合Fil 基线和 全网算力，每小时59分执行
     */
    @Scheduled(cron = "0 59 */1 * * ?")
//    @Scheduled(cron = "* */3 * * * ?")
    public void run(){
        log.info("======================FilBaselinePowerDayAggTask-start===================");
        if("dev".equals(env)){
            log.info("开发环境,不执行");
            return;
        }
        //全网算力、累计出块份数
        List<FilReportNetworkData> list = reportNetworkDataService.list();
        FilReportNetworkData data = list.get(0);
        log.info("全网算力、累计出块份数:{}", JSON.toJSONString(data));

        LocalDateTime date = DateUtils.lDTBeforeLocalDateTimeHourDate();
        log.info("date：【{}】",date);

        FilBaselinePowerHourAgg filBaselinePowerHourAgg = new FilBaselinePowerHourAgg();
        filBaselinePowerHourAgg.setBlocks(data.getBlocks())
                .setDate(date)
                .setCreateTime(LocalDateTime.now());
        baselinePowerDayAggService.save(filBaselinePowerHourAgg);
        log.info("======================FilBaselinePowerDayAggTask-end===================");
    }




}
