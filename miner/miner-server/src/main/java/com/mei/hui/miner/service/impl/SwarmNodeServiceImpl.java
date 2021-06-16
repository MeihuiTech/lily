package com.mei.hui.miner.service.impl;

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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        query.orderByDesc(SwarmNode::getCreateTime);
        IPage<SwarmNode> page = this.page(new Page<>(bo.getPageNum(), bo.getPageSize()), query);
        List<NodePageListVO> list = page.getRecords().stream().map(v -> {
            NodePageListVO vo = new NodePageListVO();
            BeanUtils.copyProperties(v, vo);
            return vo;
        }).collect(Collectors.toList());

        log.info("获取节点的每日出票信息");

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
       // LocalDate.now().
        param.put("startDate","");
        param.put("endDate","");
        List<Map<String, Object>> perTicketInfos = swarmAggMapper.getPerTicketInfo(param);
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


}
