package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mei.hui.miner.entity.SwarmNode;
import com.mei.hui.miner.entity.SwarmOneDayAgg;
import com.mei.hui.miner.mapper.SwarmNodeMapper;
import com.mei.hui.miner.mapper.SwarmOneDayAggMapper;
import com.mei.hui.miner.service.ISwarmNodeService;
import com.mei.hui.miner.service.SwarmOneDayAggService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * swarm币定时任务，每天执行一次
 */
@Configuration
@EnableScheduling
@Slf4j
public class SwarmOneDayAggTask {
    @Autowired
    private ISwarmNodeService swarmNodeService;
    @Autowired
    private SwarmOneDayAggService swarmOneDayAggService;

    @Value("${spring.profiles.active}")
    private String env;

    @Scheduled(cron = "55 59 23 */1 * ?")
    public void run() {
        log.info("======================SwarmOneHourAggTask-start===================");
        if("dev".equals(env)){
            log.info("开发环境,不执行");
            return;
        }
        /**
         * 查询所有节点
         */
        List<SwarmNode> nodes = swarmNodeService.list();
        log.info("查询所有节点数量:{}",nodes.size());
        if(nodes.size() ==0){
            return;
        }
        /**
         * 查询节点昨日数据
         */
        List<Long> nodeIds = nodes.stream().map(v -> v.getId()).collect(Collectors.toList());
        LambdaQueryWrapper<SwarmOneDayAgg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SwarmOneDayAgg::getNodeId,nodeIds);
        queryWrapper.eq(SwarmOneDayAgg::getDate, LocalDate.now().minusDays(1));
        List<SwarmOneDayAgg> swarmOneDayAggList = swarmOneDayAggService.list(queryWrapper);
        log.info("查询节点昨日数据:{}",JSON.toJSONString(swarmOneDayAggList));
        Map<Long,SwarmOneDayAgg> map = new HashMap<>();
        swarmOneDayAggList.stream().forEach(v->{
            map.put(v.getNodeId(),v);
        });

        List<SwarmOneDayAgg> batch = new ArrayList<>();
        for(SwarmNode node : nodes){
            SwarmOneDayAgg swarmOneDayAgg = map.get(node.getId());
            long perTicketAvail = node.getTicketAvail();
            long perTicketValid = node.getTicketValid();
            if(swarmOneDayAgg != null){
                perTicketAvail = node.getTicketAvail() - swarmOneDayAgg.getTicketAvail();
                perTicketValid = node.getTicketValid() - swarmOneDayAgg.getTicketValid();
            }
            SwarmOneDayAgg oneDayAgg = new SwarmOneDayAgg();
            oneDayAgg.setNodeId(node.getId());
            oneDayAgg.setPerTicketAvail(perTicketAvail);
            oneDayAgg.setPerTicketValid(perTicketValid);
            oneDayAgg.setTicketAvail(node.getTicketAvail());
            oneDayAgg.setTicketValid(node.getTicketValid());
            oneDayAgg.setDate(LocalDate.now());
            oneDayAgg.setCreateTime(LocalDateTime.now());
            batch.add(oneDayAgg);
        }
        if(batch.size() == 0){
            return;
        }
        swarmOneDayAggService.saveBatch(batch);
        log.info("======================SwarmOneHourAggTask-end===================");
    }
}
