package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mei.hui.miner.entity.SwarmAgg;
import com.mei.hui.miner.entity.SwarmNode;
import com.mei.hui.miner.entity.TotalMoneyAndTicketNum;
import com.mei.hui.miner.manager.UserManager;
import com.mei.hui.miner.mapper.SwarmNodeMapper;
import com.mei.hui.miner.service.ISwarmAggService;
import com.mei.hui.miner.service.ISwarmNodeService;
import com.mei.hui.user.feign.vo.SysUserOut;
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

/**
 * swarm币定时任务，每小时执行一次
 */
@Configuration
@EnableScheduling
@Slf4j
public class SwarmOneHourAggTask {
    @Value("${spring.profiles.active}")
    private String env;

    @Autowired
    private ISwarmAggService swarmAggService;
    @Autowired
    private SwarmNodeMapper swarmNodeMapper;
    @Autowired
    private UserManager userManager;

    //每天的整点执行
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void run() {
        log.info("======================SwarmOneHourAggTask-start===================");
        /*if("dev".equals(env)){
            log.info("开发环境,不执行");
            return;
        }*/
        log.info("userManager:【{}】",userManager);
        List<SysUserOut> users = userManager.findAllUser();
        log.info("查询所有用户数:{}",users.size());

        List<SwarmAgg> aggs = new ArrayList<>();
        for(SysUserOut user : users){
            TotalMoneyAndTicketNum r = swarmNodeMapper.getTotalMoneyAndTicketNum(user.getUserId());
            log.info("用户:{}的聚合数据:{}",user.getUserId(),JSON.toJSONString(r));
            if(r == null || r.getTotalSize().longValue() == 0){
                continue;
            }
            BigDecimal totalLinkNum = r.getTotalLinkNum();
            BigDecimal totalTicketValid = r.getTotalTicketValid();
            BigDecimal totalTicketAvail = r.getTotalTicketAvail();
            BigDecimal totalMoney = r.getTotalMoney();
            BigDecimal totalChanged = r.getTotalChanged();
            BigDecimal convertBzz = null;
            if(totalMoney != null && totalChanged != null){
                convertBzz = totalMoney.subtract(totalChanged);
            }
            SwarmAgg agg = new SwarmAgg();
            agg.setUserId(user.getUserId());
            agg.setLinkNum(totalLinkNum != null ? totalLinkNum.longValue() : 0);
            agg.setTicketAvail(totalTicketAvail != null ? totalTicketAvail.longValue() : 0);
            agg.setTicketValid(totalTicketValid != null ? totalTicketValid.longValue() : 0);
            agg.setConvertBzz(convertBzz);
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
