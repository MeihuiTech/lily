package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.entity.SwarmNode;
import com.mei.hui.miner.entity.SwarmTicket;
import com.mei.hui.miner.feign.vo.TicketPageListBO;
import com.mei.hui.miner.feign.vo.TicketPageListVO;
import com.mei.hui.miner.mapper.SwarmTicketMapper;
import com.mei.hui.miner.service.ISwarmNodeService;
import com.mei.hui.miner.service.ISwarmTicketService;
import com.mei.hui.util.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 14:16
 **/
@Service
@Slf4j
public class SwarmTicketServiceImpl extends ServiceImpl<SwarmTicketMapper,SwarmTicket> implements ISwarmTicketService {

    public PageResult<TicketPageListVO> ticketPageList(TicketPageListBO bo){

        log.info("查询票信息");
        LambdaQueryWrapper<SwarmTicket> query = new LambdaQueryWrapper<>();
        query.eq(SwarmTicket::getUserId,HttpRequestUtil.getUserId());
        if(StringUtils.isNotEmpty(bo.getNodeIp())){
            query.eq(SwarmTicket::getNodeIp,bo.getNodeIp());
        }
        if(bo.getType() != null){
            query.eq(SwarmTicket::getType,bo.getType());
        }
        IPage<SwarmTicket> page = this.page(new Page<>(bo.getPageNum(), bo.getPageSize()), query);
        List<TicketPageListVO> list = page.getRecords().stream().map(v -> {
            TicketPageListVO vo = new TicketPageListVO();
            BeanUtils.copyProperties(v, vo);
            return vo;
        }).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(),list);
    }
}
