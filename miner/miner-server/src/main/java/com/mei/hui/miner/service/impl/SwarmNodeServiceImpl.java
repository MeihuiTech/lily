package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.miner.feign.vo.SwarmTicketValidVO;
import com.mei.hui.miner.mapper.SwarmNodeMapper;
import com.mei.hui.miner.service.ISwarmNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 14:16
 **/
@Service
@Slf4j
public class SwarmNodeServiceImpl implements ISwarmNodeService {

    @Autowired
    private SwarmNodeMapper swarmNodeMapper;

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
