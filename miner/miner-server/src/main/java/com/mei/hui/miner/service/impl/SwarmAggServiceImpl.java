package com.mei.hui.miner.service.impl;

import com.mei.hui.miner.entity.SwarmAgg;
import com.mei.hui.miner.mapper.SwarmAggMapper;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SwarmAgg;
import com.mei.hui.miner.entity.SwarmNode;
import com.mei.hui.miner.feign.vo.ConvertBzzVO;
import com.mei.hui.miner.feign.vo.LinkVO;
import com.mei.hui.miner.feign.vo.SwarmHomePageVO;
import com.mei.hui.miner.feign.vo.TicketValidVO;
import com.mei.hui.miner.mapper.SwarmAggMapper;
import com.mei.hui.miner.service.ISwarmAggService;
import com.mei.hui.miner.service.ISwarmNodeService;
import com.mei.hui.util.CurrencyEnum;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.assertj.core.util.diff.myers.MyersDiff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private ISwarmAggService swarmAggService;

    @Autowired
    private SwarmAggMapper swarmAggMapper;

    public Result<SwarmHomePageVO> homePage(){
        if(CurrencyEnum.BZZ.getCurrencyId() != HttpRequestUtil.getCurrencyId()){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"当前选择币种不是swarm");
        }
        SwarmHomePageVO swarmHomePageVO = new SwarmHomePageVO();
        //总出票资产、有效出票数、无效出票数
        putTotalMoneyAndTicketNum(swarmHomePageVO);
        log.info("连接数");
        putLinks(swarmHomePageVO);

        log.info("有效票数");
        putTicketValids(swarmHomePageVO);

        log.info("可兑换BZZ");
        putConvertBzz(swarmHomePageVO);
        return Result.success(swarmHomePageVO);
    }

    /**
     * 近30天有效票数数图表数据
     * @param swarmHomePageVO
     */
    public void putConvertBzz(SwarmHomePageVO swarmHomePageVO){
        ConvertBzzVO convertBzzVO = new ConvertBzzVO();
        convertBzzVO.setDateTime(LocalDateTime.now());
        convertBzzVO.setConvertBzz(new BigDecimal(100));
        swarmHomePageVO.getBzz().add(convertBzzVO);
    }

    /**
     * 近30天有效票数数图表数据
     * @param swarmHomePageVO
     */
    public void putTicketValids(SwarmHomePageVO swarmHomePageVO){
        TicketValidVO ticketValidVO = new TicketValidVO();
        ticketValidVO.setDateTime(LocalDateTime.now());
        ticketValidVO.setTicketValidNum(100L);
        swarmHomePageVO.getTicketValids().add(ticketValidVO);
    }

    /**
     * 近30天连接数图表数据
     * @param swarmHomePageVO
     */
    public void putLinks(SwarmHomePageVO swarmHomePageVO){

       /* LambdaQueryWrapper<SwarmAgg> swarmAggQuery = new LambdaQueryWrapper<>();
        swarmAggQuery
        swarmAggService.list(swarmAggQuery);*/
        LinkVO linkVO = new LinkVO();
        linkVO.setDateTime(LocalDateTime.now());
        linkVO.setLinkNum(100L);
        swarmHomePageVO.getLinks().add(linkVO);
    }

    /**
     * 出票资产：所有节点出票资产的总和
     * 累计出票数：有效出票数、无效出票数
     * 在线节点、离线节点、连接数
     */
    public void putTotalMoneyAndTicketNum(SwarmHomePageVO swarmHomePageVO){
        Long userId = HttpRequestUtil.getUserId();
        /**
         * 资产数据,计算所有节点资产的总和
         */
        LambdaQueryWrapper<SwarmNode> nodeQuery = new LambdaQueryWrapper<>();
        nodeQuery.eq(SwarmNode::getUserid,userId);
        log.info("查询用户的节点信息,入参:userId = {}",userId);
        List<SwarmNode> nodes = swarmNodeService.list(nodeQuery);
        log.info("查询用户的节点信息,出参:{}", JSON.toJSONString(nodes));
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
     * @param swarmHomePageVO
     */
    public void putYesterdayTicketNum(SwarmHomePageVO swarmHomePageVO){
        Long userId = HttpRequestUtil.getUserId();

    }


    /**
     * 根据userId、昨天开始时间、昨天结束时间 在聚合统计表里获取昨天的总有效出票数
     * @param userId
     * @param beginYesterdayDate
     * @param endYesterdayDate
     * @return
     */
    @Override
    public Long selectYesterdayTicketValid(Long userId, Date beginYesterdayDate, Date endYesterdayDate) {
        return swarmAggMapper.selectYesterdayTicketValid(userId, beginYesterdayDate, endYesterdayDate);
    }
}
