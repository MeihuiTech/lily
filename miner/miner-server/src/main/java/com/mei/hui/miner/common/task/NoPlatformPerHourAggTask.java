package com.mei.hui.miner.common.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.entity.NoPlatformMiner;
import com.mei.hui.miner.entity.NoPlatformPerHourAgg;
import com.mei.hui.miner.service.NoPlatformMinerService;
import com.mei.hui.miner.service.NoPlatformPerHourAggService;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.html.DateFormatEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@Slf4j
public class NoPlatformPerHourAggTask {
    @Autowired
    private NoPlatformPerHourAggService noPlatformPerHourAggService;

    @Autowired
    private NoPlatformMinerService noPlatformMinerService;
    @Value("${spring.profiles.active}")
    private String env;
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 非平台矿工每小时出块数
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void run(){
        log.info("======================NoPlatformPerHourAggTask-start===================");
        if("dev".equals(env)){
            log.info("开发环境,不执行");
            return;
        }
        try {
            LambdaQueryWrapper<NoPlatformMiner> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(NoPlatformMiner::getStatus,0);
            queryWrapper.eq(NoPlatformMiner::getType,1);
            List<NoPlatformMiner> miners = noPlatformMinerService.list(queryWrapper);
            List<NoPlatformPerHourAgg> list = new ArrayList<>();
            for(NoPlatformMiner miner : miners){
                Long blocks = noPlatformPerHourAggService.getPreNoPlatformPerHourAgg(miner.getMinerId());
                BigDecimal perHoureBlocks = new BigDecimal(miner.getTotalBlocks()).subtract(new BigDecimal(blocks));
                LocalDateTime dateTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
                NoPlatformPerHourAgg noPlatformPerHourAgg = new NoPlatformPerHourAgg()
                        .setMinerId(miner.getMinerId())
                        .setPerHourBlocks(perHoureBlocks.longValue())
                        .setTotalBlocks(miner.getTotalBlocks())
                        .setCreateTime(dateTime);
                list.add(noPlatformPerHourAgg);
                String strDateTime = DateUtils.localDateTimeToString(dateTime, DateFormatEnum.YYYY_MM_DD_HH_MM_SS);
                String key = String.format("NoPlatform:%s:%s", miner.getMinerId(), strDateTime);
                redisUtil.set(key,blocks+"",3, TimeUnit.HOURS);
            }
            noPlatformPerHourAggService.saveBatch(list);
        } catch (Exception e) {
            log.error("异常:",e);
        }
        log.info("======================NoPlatformPerHourAggTask-end===================");
    }

}
