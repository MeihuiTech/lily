package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.miner.feign.vo.SwarmTicketValidVO;
import com.mei.hui.miner.mapper.SwarmNodeMapper;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SwarmNode;
import com.mei.hui.miner.feign.vo.NodePageListBO;
import com.mei.hui.miner.feign.vo.NodePageListVO;
import com.mei.hui.miner.mapper.SwarmAggMapper;
import com.mei.hui.miner.mapper.SwarmNodeMapper;
import com.mei.hui.miner.service.ISwarmAggService;
import com.mei.hui.miner.service.ISwarmNodeService;
import com.mei.hui.util.CurrencyEnum;
import com.mei.hui.util.MyException;
import com.mei.hui.util.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 14:16
 **/
@Service
@Slf4j
public class SwarmNodeServiceImpl extends ServiceImpl<SwarmNodeMapper, SwarmNode> implements ISwarmNodeService {

    @Autowired
    private SwarmAggMapper swarmAggMapper;


    @Autowired
    private SwarmNodeMapper swarmNodeMapper;


    public PageResult<NodePageListVO> nodePageList(NodePageListBO bo){
        if(CurrencyEnum.BZZ.getCurrencyId() != HttpRequestUtil.getCurrencyId()){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"当前选择币种不是swarm");
        }
        LambdaQueryWrapper<SwarmNode> query = new LambdaQueryWrapper();
        if(StringUtils.isNotEmpty(bo.getIp())){
            query.eq(SwarmNode::getId,bo.getIp());
        }
        if(bo.getState() != null){
            query.eq(SwarmNode::getState,bo.getState());
        }
        if("ticketValid".equalsIgnoreCase(bo.getCloumName())){
            if(bo.isAsc()){
                query.orderByAsc(SwarmNode::getTicketAvail);
            }else{
                query.orderByDesc(SwarmNode::getTicketAvail);
            }
        }
        if("linkNum".equalsIgnoreCase(bo.getCloumName())){
            if(bo.isAsc()){
                query.orderByAsc(SwarmNode::getLinkNum);
            }else{
                query.orderByDesc(SwarmNode::getLinkNum);
            }
        }
        query.orderByDesc(SwarmNode::getCreateTime);
        IPage<SwarmNode> page = this.page(new Page<>(bo.getPageNum(), bo.getPageSize()), query);
        List<NodePageListVO> list = page.getRecords().stream().map(v -> {
            NodePageListVO vo = new NodePageListVO();
            BeanUtils.copyProperties(v, vo);
            return vo;
        }).collect(Collectors.toList());

        log.info("获取节点的每日出票信息");
        putYestodayTicketValid(list);

        return new PageResult(page.getTotal(),list);
    }

    /**
     * 组装昨日有效出票数
     * @param list
     */
    public void putYestodayTicketValid(List<NodePageListVO> list){
        List<String> addresses = list.stream().map(v ->v.getWalletAddress()).collect(Collectors.toList());
        Map<String,Object> param = new HashMap<>();
        param.put("list",addresses);
        param.put("startDate",LocalDate.now().minusDays(1));
        param.put("endDate",LocalDate.now());
        log.info("查询节点每天的出票数，入参:{}", JSON.toJSONString(param));
        List<Map<String, Object>> perTicketInfos = swarmAggMapper.getPerTicketInfo(param);
        log.info("查询节点每天的出票数，出参:{}", JSON.toJSONString(perTicketInfos));
        if(perTicketInfos.size() > 0){
            Map<String, Long> perTickets = new HashMap<>();
            perTicketInfos.stream().forEach(v->{
                String key = (String) v.get("walletAddress");
                Long value = (Long) v.get("totalPerTicket");
                perTickets.put(key,value);
            });
            list.stream().forEach(v->{
                Long totalPerTicket = perTickets.get(v.getWalletAddress());
                v.setYestodayTicketValid(totalPerTicket);
            });
        }
    }

    /**
     * 管理员首页-平台概览-总有效出票数，用的字段：有效出票数
     * @return
     */
    @Override
    public Long selectTicketValid() {
        return swarmNodeMapper.selectTicketValid();
    }

    /**
     * 管理员首页-平台概览-昨日有效出票份数
     * @return
     */
    @Override
    public Long selectYesterdayTicketValid(Date beginYesterdayDate, Date endYesterdayDate) {
        return swarmNodeMapper.selectYesterdayTicketValid(beginYesterdayDate, endYesterdayDate);
    }

    /**
     * 管理员首页-平台概览-有效节点
     * @return
     */
    @Override
    public Long selectNodeValid() {
        return swarmNodeMapper.selectNodeValid();
    }

    /**
     * 管理员首页-平台概览-平台总连接数
     * @return
     */
    @Override
    public Long selectLinkNum() {
        return swarmNodeMapper.selectLinkNum();
    }

    /**
    * 管理员首页-平台有效出票数排行榜
    *
    * @description
    * @author shangbin
    * @date 2021/6/16 17:57
    * @param [swarmTicketValidVOPage, ticketValid]
    * @return com.baomidou.mybatisplus.core.metadata.IPage<com.mei.hui.miner.feign.vo.SwarmTicketValidVO>
    * @version v1.0.0
    */
    @Override
    public IPage<SwarmTicketValidVO> ticketValidPage(Page<SwarmTicketValidVO> swarmTicketValidVOPage, Long ticketValid) {
        return swarmNodeMapper.ticketValidPage(swarmTicketValidVOPage, ticketValid);
    }
}
