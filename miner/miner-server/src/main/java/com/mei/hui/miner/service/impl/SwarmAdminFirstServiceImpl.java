package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.feign.vo.SwarmAdminFirstCollectVO;
import com.mei.hui.miner.feign.vo.SwarmTicketValidVO;
import com.mei.hui.miner.service.ISwarmAdminFirstService;
import com.mei.hui.miner.service.ISwarmAggService;
import com.mei.hui.miner.service.ISwarmNodeService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SwarmAdminFirstServiceImpl implements ISwarmAdminFirstService {

    @Autowired
    private ISwarmNodeService swarmNodeService;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ISwarmAggService swarmAggService;

    /**
     * fil管理员首页-平台概览
     * @return
     */
    @Override
    public SwarmAdminFirstCollectVO swarmAdminFirstAllCount() {
        SwarmAdminFirstCollectVO swarmAdminFirstCollectVO = new SwarmAdminFirstCollectVO();
        // 管理员首页-平台概览-总有效出票数，用的字段：有效出票数
        Long ticketValid = swarmNodeService.selectTicketValid();
        swarmAdminFirstCollectVO.setTicketValid(ticketValid);
        // 管理员首页-平台概览-今日有效出票份数
        // 获取昨天的日期
        String yesterDayDateYmd = DateUtils.getYesterDayDateYmd();
        Long yesterdayTicketValid = swarmNodeService.selectYesterdayTicketValid(yesterDayDateYmd);
        if (yesterdayTicketValid != null){
            swarmAdminFirstCollectVO.setTodayTicketValid(ticketValid - yesterdayTicketValid);
        } else {
            swarmAdminFirstCollectVO.setTodayTicketValid(ticketValid);
        }
        // 管理员首页-平台概览-有效节点
        Long nodeValid = swarmNodeService.selectNodeValid();
        swarmAdminFirstCollectVO.setNodeValid(nodeValid);
        // 管理员首页-平台概览-平台总连接数
        Long linkNum = swarmNodeService.selectLinkNum();
        swarmAdminFirstCollectVO.setLinkNum(linkNum);
        return swarmAdminFirstCollectVO;
    }

    /**
     * swarm管理员首页-平台有效出票数排行榜`
     * @param basePage
     * @return
     */
    @Override
    public Map<String,Object> ticketValidPage(BasePage basePage) {
        // 管理员首页-平台概览-总有效出票数，用的字段：有效出票数
        Long ticketValid = swarmNodeService.selectTicketValid();
        log.info("管理员首页-平台概览-总有效出票数出参：【{}】",ticketValid);
        Page<SwarmTicketValidVO> swarmTicketValidVOPage = new Page<>(basePage.getPageNum(),basePage.getPageSize());
        IPage<SwarmTicketValidVO> result = swarmNodeService.ticketValidPage(swarmTicketValidVOPage,ticketValid);
        log.info("管理员首页-平台有效出票数排行榜出参:【{}】",JSON.toJSON(result));
        if (result.getTotal() > 0) {
            for (SwarmTicketValidVO swarmTicketValidVO:result.getRecords()) {
                log.info("swarmTicketValidVO修改值之前:【{}】",JSON.toJSON(swarmTicketValidVO));
                Long userId = HttpRequestUtil.getUserId();
                // 获取昨天的日期
                String yesterDayDateYmd = DateUtils.getYesterDayDateYmd();
                log.info("根据userId：【{}】,昨天日期：【{}】,在聚合统计表里获取昨天的总有效出票数入参",userId,yesterDayDateYmd);
                Long yesterdayTicketValid = swarmAggService.selectYesterdayTicketValid(userId,yesterDayDateYmd);
                log.info("根据userId、昨天日期 在聚合统计表里获取昨天的总有效出票数出参：【{}】",yesterdayTicketValid);
                if (yesterdayTicketValid != null){
                    swarmTicketValidVO.setTodayTicketValid(swarmTicketValidVO.getTicketValid() - yesterdayTicketValid);
                } else {
                    swarmTicketValidVO.setTodayTicketValid(swarmTicketValidVO.getTicketValid());
                }
                log.info("swarmTicketValidVO修改值之后:【{}】",JSON.toJSON(swarmTicketValidVO));
                swarmTicketValidVO.setTicketValidPercent(BigDecimalUtil.formatTwo(swarmTicketValidVO.getTicketValidPercent().multiply(new BigDecimal(100))));

                SysUserOut sysUserOut = new SysUserOut();
                sysUserOut.setUserId(swarmTicketValidVO.getUserId());
                log.info("查询用户姓名入参：【{}】",JSON.toJSON(sysUserOut));
                Result<SysUserOut> sysUserOutResult = userFeignClient.getUserById(sysUserOut);
                log.info("查询用户姓名出参：【{}】",JSON.toJSON(sysUserOutResult));
                if(ErrorCode.MYB_000000.getCode().equals(sysUserOutResult.getCode())){
                    swarmTicketValidVO.setUserName(sysUserOutResult.getData().getUserName());
                }

            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",result.getRecords());
        map.put("total",result.getTotal());
        return map;
    }


}
