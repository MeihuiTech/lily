package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.mei.hui.miner.entity.FilBaselinePowerDayAgg;
import com.mei.hui.miner.entity.FilReportNetworkData;
import com.mei.hui.miner.service.FilBaselinePowerDayAggService;
import com.mei.hui.miner.service.FilReportNetworkDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 按天聚合Fil 基线和 全网算力
 */
@Configuration
@EnableScheduling
@Slf4j
public class FilBaselinePowerDayAggTask {

    @Autowired
    private FilReportNetworkDataService reportNetworkDataService;
    @Autowired
    private FilBaselinePowerDayAggService baselinePowerDayAggService;
    @Value("${spring.profiles.active}")
    private String env;

    @Scheduled(cron = "55 59 23 */1 * ?")
    public void run(){
        log.info("======================FilBaselinePowerDayAggTask-start===================");
        if("dev".equals(env)){
            log.info("开发环境,不执行");
            return;
        }
        //获取基线数值
        BigDecimal baseLine = baseLineValue();
        log.info("基线值:{}",baseLine);

        //全网算力、累计出块份数
        List<FilReportNetworkData> list = reportNetworkDataService.list();
        FilReportNetworkData data = list.get(0);
        log.info("全网算力、累计出块份数:{}", JSON.toJSONString(data));

        FilBaselinePowerDayAgg baselinePowerDayAgg = new FilBaselinePowerDayAgg();
        baselinePowerDayAgg.setPower(data.getPower())
                .setBlocks(data.getBlocks())
                .setBaseLine(baseLine).setDate(LocalDate.now()).setCreateTime(LocalDateTime.now());
        baselinePowerDayAggService.save(baselinePowerDayAgg);
        log.info("======================FilBaselinePowerDayAggTask-end===================");
    }


    public BigDecimal baseLineValue(){
        Long t = getSecond();
        BigDecimal baseInit = new BigDecimal("2.50571167981217") //E
                .multiply(new BigDecimal(1024)) //P
                .multiply(new BigDecimal(1024)) //T
                .multiply(new BigDecimal(1024)) //G
                .multiply(new BigDecimal(1024)) //M
                .multiply(new BigDecimal(1024))//KB
                .multiply(new BigDecimal(1024)); //B
        double ss = Math.log(2);
        //10770900 t为从主网到现在的时间距离（秒为单位），
        BigDecimal gt = new BigDecimal(ss).divide(new BigDecimal("31536000"),20, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(t));
        double egt = Math.exp(gt.doubleValue());
        BigDecimal result = new BigDecimal(egt).multiply(baseInit);
        //log.info("结果:{}",result.setScale(2,BigDecimal.ROUND_HALF_UP));
        return result.setScale(4,BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 获取从fil主网上线到现在的时间间隔
     * @return
     */
    public Long getSecond(){
        long now = LocalDateTime.now()
                .toEpochSecond(ZoneOffset.of("+8"));
        long base = LocalDateTime.now().withYear(2020).withMonth(8).withDayOfMonth(25).withHour(6).withMinute(0).withSecond(30)
                .toEpochSecond(ZoneOffset.of("+8"));
        long t = now - base;
        //log.info("从主网到现在的时间:{}",t);
        return t;
    }




}
