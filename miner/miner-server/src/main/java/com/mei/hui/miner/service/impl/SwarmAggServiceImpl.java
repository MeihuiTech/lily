package com.mei.hui.miner.service.impl;

import com.mei.hui.miner.entity.*;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.SwarmAggMapper;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SwarmAgg;
import com.mei.hui.miner.mapper.SwarmAggMapper;
import com.mei.hui.miner.mapper.SwarmOneDayAggMapper;
import com.mei.hui.miner.service.ISwarmAggService;
import com.mei.hui.miner.service.ISwarmNodeService;
import com.mei.hui.util.CurrencyEnum;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.assertj.core.util.diff.myers.MyersDiff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 14:16
 **/
@Service
@Slf4j
public class SwarmAggServiceImpl extends ServiceImpl<SwarmAggMapper, SwarmAgg> implements ISwarmAggService  {

    @Autowired
    private ISwarmNodeService swarmNodeService;
    @Autowired
    private SwarmOneDayAggMapper swarmOneDayAggMapper;

    @Autowired
    private SwarmAggMapper swarmAggMapper;

    public Result<SwarmHomePageVO> homePage(){
        if(CurrencyEnum.BZZ.getCurrencyId() != HttpRequestUtil.getCurrencyId()){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"当前选择币种不是swarm");
        }
        SwarmHomePageVO swarmHomePageVO = new SwarmHomePageVO();
        /**
         * 查询当前用户所有的节点
         */
        Long userId = HttpRequestUtil.getUserId();
        LambdaQueryWrapper<SwarmNode> nodeQuery = new LambdaQueryWrapper<>();
        nodeQuery.eq(SwarmNode::getUserid,userId);
        log.info("查询用户的节点信息,入参:userId = {}",userId);
        List<SwarmNode> nodes = swarmNodeService.list(nodeQuery);
        log.info("查询用户的节点信息,出参:{}", JSON.toJSONString(nodes));
        if(nodes.size() == 0){
            return Result.success(swarmHomePageVO);
        }

        log.info("总出票资产、有效出票数、无效出票数");
        putTotalMoneyAndTicketNum(swarmHomePageVO,nodes);

        log.info("昨日有效出票数、昨日无效出票数");
        putYesterdayTicketAvailAndValid(swarmHomePageVO,nodes);

        log.info("图表显示数据");
        putChart(swarmHomePageVO,nodes);

        return Result.success(swarmHomePageVO);
    }

    /**
     * 近30天连接数图表数据
     * @param swarmHomePageVO
     */
    public void putChart(SwarmHomePageVO swarmHomePageVO,List<SwarmNode> nodes){
        List<Long> nodeIds = nodes.stream().map(v -> v.getId()).collect(Collectors.toList());
        log.info("查询近30的连接数");
        List<FindChart> list = swarmAggMapper.findChart(LocalDate.now().minusDays(30), LocalDateTime.now(), nodeIds);
        log.info("查询近30的连接数,出参:{}",JSON.toJSONString(list));

        //连接数图表数据
        List<LinkVO> links = new ArrayList<>();
        //有效票数图表数据
        List<TicketValidVO> ticketValids = new ArrayList<>();
        //可兑换BZZ图表数据
        List<ConvertBzzVO> bzz = new ArrayList<>();
        for(FindChart findChart : list){
            LinkVO linkVO = new LinkVO(findChart.getTotallinkNum(),findChart.getDate());
            links.add(linkVO);
            TicketValidVO ticketValidVO = new TicketValidVO(findChart.getTotalTicketValid(),findChart.getDate());
            ticketValids.add(ticketValidVO);
            ConvertBzzVO convertBzzVO = new ConvertBzzVO(findChart.getTotalConvertBzz(),findChart.getDate());
            bzz.add(convertBzzVO);
        }
        swarmHomePageVO.setLinks(links);
        swarmHomePageVO.setTicketValids(ticketValids);
        swarmHomePageVO.setBzz(bzz);
    }

    /**
     * 出票资产：所有节点出票资产的总和
     * 累计出票数：有效出票数、无效出票数
     * 在线节点、离线节点、连接数
     */
    public void putTotalMoneyAndTicketNum(SwarmHomePageVO swarmHomePageVO,List<SwarmNode> nodes){
       /**
         * 资产数据,计算所有节点资产的总和
         */
        //总出票资产
        BigDecimal totalMoney = new BigDecimal("0");
        //累计有效出票数
        BigDecimal totalTicketValid = new BigDecimal("0");
        //累计无效出票数
        BigDecimal totalTicketAvail = new BigDecimal("0");
        //总的连接数
        BigDecimal totalLinkNum = new BigDecimal("0");
        long onlineNodeNum = 0;//在线节点数
        long offlineNodeNum = 0;//离线节点数
        for(SwarmNode node : nodes){
            totalMoney = totalMoney.add(node.getMoney());
            totalTicketValid = totalTicketValid.add(new BigDecimal(node.getTicketValid()));
            totalTicketAvail = totalTicketAvail.add(new BigDecimal(node.getTicketAvail()));
            totalLinkNum = totalLinkNum.add(new BigDecimal(node.getLinkNum()));
            if (node.getState() == 1) {
                onlineNodeNum++;
            } else {
                offlineNodeNum++;
            }
        }
        swarmHomePageVO.setTotalMoney(totalMoney);
        swarmHomePageVO.setTotalTicketValid(totalTicketValid.longValue());
        swarmHomePageVO.setTotalTicketAvail(totalTicketAvail.longValue());
        swarmHomePageVO.setTotalLinkNum(totalLinkNum.longValue());
        swarmHomePageVO.setOfflineNodeNum(offlineNodeNum);
        swarmHomePageVO.setOnlineNodeNum(onlineNodeNum);
    }

    /**
     * 昨日有效出票数、昨日无效出票数
     */
    public void putYesterdayTicketAvailAndValid(SwarmHomePageVO swarmHomePageVO,List<SwarmNode> nodes){
        List<Long> nodeIds = nodes.stream().map(v -> v.getId()).collect(Collectors.toList());
        LambdaQueryWrapper<SwarmOneDayAgg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SwarmOneDayAgg::getNodeId,nodeIds);
        queryWrapper.eq(SwarmOneDayAgg::getDate,LocalDate.now().minusDays(1));
        List<SwarmOneDayAgg> swarmOneDayAggList = swarmOneDayAggMapper.selectList(queryWrapper);

        log.info("查询节点每天的出票数，出参:{}", JSON.toJSONString(swarmOneDayAggList));
        if(swarmOneDayAggList == null || swarmOneDayAggList.size() == 0){
            return;
        }
        long yesterdayTicketAvail = 0;
        long yesterdayTicketValid = 0;
        for(SwarmOneDayAgg swarmOneDayAgg :swarmOneDayAggList ){
            yesterdayTicketAvail +=swarmOneDayAgg.getPerTicketAvail();
            yesterdayTicketValid += swarmOneDayAgg.getPerTicketValid();
        }
        log.info("昨日的有效出票数:{},昨天无效出票数:{}",yesterdayTicketValid,yesterdayTicketAvail);
        swarmHomePageVO.setYesterdayTicketAvail(yesterdayTicketAvail);
        swarmHomePageVO.setYesterdayTicketValid(yesterdayTicketValid);
    }

    /**
     * 根据userId、昨天时间 在聚合统计表里获取昨天的总有效出票数
     * @param userId
     * @return
     */
    @Override
    public Long selectYesterdayTicketValid(Long userId, String yesterDayDateYmd) {
        return swarmAggMapper.selectYesterdayTicketValid(userId, yesterDayDateYmd);
    }

    // 根据userID查询有效节点
    @Override
    public Long selectNodeValid(Long userId) {
        return swarmAggMapper.selectNodeValid(userId);
    }
}
