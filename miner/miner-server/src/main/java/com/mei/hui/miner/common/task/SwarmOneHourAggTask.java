package com.mei.hui.miner.common.task;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * swarm币定时任务，没一小时执行一次
 */
@Configuration
@EnableScheduling
public class SwarmOneHourAggTask {

    @Scheduled(cron = "55 59 23 */1 * ?")
    public void run() {





    }
}
