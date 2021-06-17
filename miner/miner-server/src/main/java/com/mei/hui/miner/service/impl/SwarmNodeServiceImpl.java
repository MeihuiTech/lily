package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.PerTicket;
import com.mei.hui.miner.entity.SwarmNode;
import com.mei.hui.miner.feign.vo.NodePageListBO;
import com.mei.hui.miner.feign.vo.NodePageListVO;
import com.mei.hui.miner.mapper.SwarmAggMapper;
import com.mei.hui.miner.mapper.SwarmNodeMapper;
import com.mei.hui.miner.service.ISwarmNodeService;
import com.mei.hui.util.CurrencyEnum;
import com.mei.hui.util.MyException;
import com.mei.hui.util.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
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

    public PageResult<NodePageListVO> nodePageList(NodePageListBO bo){
        if(CurrencyEnum.BZZ.getCurrencyId() != HttpRequestUtil.getCurrencyId()){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"当前选择币种不是swarm");
        }
        LambdaQueryWrapper<SwarmNode> query = new LambdaQueryWrapper();
        if(StringUtils.isNotEmpty(bo.getIp())){
            query.eq(SwarmNode::getNodeIp,bo.getIp());
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
        //先在聚合表统计节点和昨日出票数，然后进行排序后返回
        if("yestodayTicketValid".equalsIgnoreCase(bo.getCloumName())){
            List<String> adrresses = perTicketPageList(bo);
            if(adrresses != null && adrresses.size() > 0){
                query.in(SwarmNode::getWalletAddress,adrresses);
            }
        }
        query.orderByDesc(SwarmNode::getCreateTime);
        log.info("查询节点列表，入参:{}",query.getCustomSqlSegment());
        IPage<SwarmNode> page = this.page(new Page<>(bo.getPageNum(), bo.getPageSize()), query);
        log.info("查询节点列表，出参:{}",JSON.toJSONString(page.getRecords()));
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
     * 按昨日出票数排序后返回节点地址，即节点的唯一标识
     * @param bo
     * @return
     */
    public List<String> perTicketPageList(NodePageListBO bo){
        log.info("按昨日出票进行排序,入参:isAsc={}",bo.isAsc());
        IPage<PerTicket> page = swarmAggMapper.perTicketPageList(new Page<>(bo.getPageNum(), bo.getPageSize()), bo.isAsc());
        log.info("按昨日出票进行排序,出参:{}",JSON.toJSONString(page.getRecords()));
        List<String> list = page.getRecords().stream().map(v -> v.getWalletAddress()).collect(Collectors.toList());
        log.info("按昨日出票数排序列表:{}",JSON.toJSONString(list));
        return list;
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
        List<PerTicket> perTicketInfos = swarmAggMapper.getPerTicketInfo(param);
        log.info("查询节点每天的出票数，出参:{}", JSON.toJSONString(perTicketInfos));
        if(perTicketInfos == null || perTicketInfos.size() == 0){
            return;
        }
        Map<String, Long> perTickets = new HashMap<>();
        perTicketInfos.stream().forEach(v->{
            String key = v.getWalletAddress();
            Long value = v.getTotalPerTicket();
            perTickets.put(key,value);
        });
        list.stream().forEach(v->{
            Long totalPerTicket = perTickets.get(v.getWalletAddress());
            if(totalPerTicket != null){
                v.setYestodayTicketValid(totalPerTicket);
            }
        });
    }


}
