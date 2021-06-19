package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mei.hui.miner.entity.SwarmAgg;
import com.mei.hui.miner.entity.SwarmNode;
import com.mei.hui.miner.service.ISwarmAggService;
import com.mei.hui.miner.service.ISwarmNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * swarm币定时任务，每小时执行一次
 */
@Configuration
@EnableScheduling
@Slf4j
public class SwarmOneHourAggTask {

    @Autowired
    private ISwarmAggService swarmAggService;
    @Autowired
    private ISwarmNodeService swarmNodeService;

    //每天的整点执行
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void run() {
        log.info("======================SwarmOneHourAggTask-start===================");
        List<SwarmNode> list = swarmNodeService.list();
        log.info("获取节点:{}", JSON.toJSONString(list));
        List<SwarmAgg> aggs = new ArrayList<>();
        for(SwarmNode node : list){
            log.info("当前处理节点:{}",JSON.toJSONString(node));
            SwarmAgg agg = new SwarmAgg();
            agg.setPeerId(node.getPeerId());
            agg.setLinkNum(node.getLinkNum());
            agg.setTicketAvail(node.getTicketAvail());
            agg.setTicketValid(node.getTicketValid());
            agg.setConvertBzz(node.getMoney().subtract(node.getChanged()));
            agg.setDate(LocalDateTime.now().withMinute(0).withSecond(0).withNano(0));
            agg.setCreateTime(LocalDateTime.now());
            aggs.add(agg);
        }
        if(aggs.size() ==0){
            return;
        }
        swarmAggService.saveBatch(aggs);
        log.info("======================SwarmOneHourAggTask-end===================");
    }
}
