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
import com.mei.hui.miner.mapper.SwarmNodeMapper;
import com.mei.hui.miner.mapper.SwarmOneDayAggMapper;
import com.mei.hui.miner.service.ISwarmAggService;
import com.mei.hui.miner.service.ISwarmNodeService;
import com.mei.hui.util.CurrencyEnum;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import com.netflix.ribbon.proxy.annotation.Http;
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
    @Autowired
    private SwarmNodeMapper swarmNodeMapper;

    public Result<SwarmHomePageVO> homePage(){
        if(CurrencyEnum.BZZ.getCurrencyId() != HttpRequestUtil.getCurrencyId()){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"当前选择币种不是swarm");
        }
        SwarmHomePageVO swarmHomePageVO = new SwarmHomePageVO();
        log.info("总出票资产、有效出票数、无效出票数");
        putTotalMoneyAndTicketNum(swarmHomePageVO);

        log.info("昨日有效出票数、昨日无效出票数");
        putYesterdayTicketAvailAndValid(swarmHomePageVO);

        log.info("图表显示数据");
        putChart(swarmHomePageVO);

        return Result.success(swarmHomePageVO);
    }

    /**
     * 近30天连接数图表数据
     * @param swarmHomePageVO
     */
    public void putChart(SwarmHomePageVO swarmHomePageVO){
        Long userId = HttpRequestUtil.getUserId();
        log.info("查询近30的连接数");
        List<FindChart> list = swarmAggMapper.findChart(LocalDate.now().minusDays(4), LocalDateTime.now(), userId);
        log.info("查询近30的连接数,出参:{}",list.size());

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
    public void putTotalMoneyAndTicketNum(SwarmHomePageVO swarmHomePageVO){
        Long userId = HttpRequestUtil.getUserId();
       /**
         * 资产数据,计算所有节点资产的总和
         */
        TotalMoneyAndTicketNum v = swarmNodeMapper.getTotalMoneyAndTicketNum(userId);
        log.info("统计数据:{}",JSON.toJSONString(v));
        if(v != null){
            swarmHomePageVO.setTotalMoney(v.getTotalMoney() != null ? v.getTotalMoney() : new BigDecimal(0));
            swarmHomePageVO.setTotalTicketValid(v.getTotalTicketValid() != null ? v.getTotalTicketValid().longValue() : 0);
            swarmHomePageVO.setTotalTicketAvail(v.getTotalTicketAvail() != null ? v.getTotalTicketAvail().longValue() : 0);
            swarmHomePageVO.setTotalLinkNum(v.getTotalLinkNum() != null ? v.getTotalLinkNum().longValue() : 0);
            swarmHomePageVO.setOnlineNodeNum(v.getOnlineNodeNum() != null ? v.getOnlineNodeNum().longValue() : 0);
            swarmHomePageVO.setOfflineNodeNum(v.getTotalSize()!= null && v.getOnlineNodeNum() != null ?
                    v.getTotalSize().subtract(v.getOnlineNodeNum()).longValue() : 0);
        }
    }

    /**
     * 昨日有效出票数、昨日无效出票数
     */
    public void putYesterdayTicketAvailAndValid(SwarmHomePageVO swarmHomePageVO){
        Map<String,Object> param = new HashMap<>();
        param.put("userId", HttpRequestUtil.getUserId());
        param.put("date",LocalDate.now().minusDays(1));
        Map<String, Object> v = swarmNodeMapper.getYesterdayTicketAvailAndValid(param);
        log.info("查询昨日出票情况,结果:{}",JSON.toJSONString(v));
        if(v != null && v.size() > 0){
            BigDecimal yesterdayTicketValid = (BigDecimal) v.get("totalPerTicketValid");
            BigDecimal yesterdayTicketAvail = (BigDecimal) v.get("totalPerTicketAvail");
            log.info("昨日的有效出票数:{},昨天无效出票数:{}",yesterdayTicketValid,yesterdayTicketAvail);
            swarmHomePageVO.setYesterdayTicketAvail(yesterdayTicketAvail.longValue());
            swarmHomePageVO.setYesterdayTicketValid(yesterdayTicketValid.longValue());
        }
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
}
