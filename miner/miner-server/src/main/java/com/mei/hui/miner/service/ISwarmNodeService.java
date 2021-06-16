package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.miner.feign.vo.SwarmTicketValidVO;

import java.util.Date;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 14:15
 **/
public interface ISwarmNodeService {

    /**
    * 管理员首页-平台概览-总有效出票数，用的字段：有效出票数
    *
    * @description
    * @author shangbin
    * @date 2021/6/16 15:08
    * @param []
    * @return java.lang.Long
    * @version v1.0.0
    */
    public Long selectTicketValid();

    /**
    * 管理员首页-平台概览-昨日有效出票份数
    *
    * @description
    * @author shangbin
    * @date 2021/6/16 15:08
    * @param []
    * @return java.lang.Long
    * @version v1.0.0
    */
    public Long selectYesterdayTicketValid(Date beginYesterdayDate,Date endYesterdayDate);

    /**
    * 管理员首页-平台概览-有效节点
    *
    * @description
    * @author shangbin
    * @date 2021/6/16 15:09
    * @param []
    * @return java.lang.Long
    * @version v1.0.0
    */
    public Long selectNodeValid();

    /**
    * 管理员首页-平台概览-平台总连接数
    *
    * @description
    * @author shangbin
    * @date 2021/6/16 15:09
    * @param []
    * @return java.lang.Long
    * @version v1.0.0
    */
    public Long selectLinkNum();

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
    public IPage<SwarmTicketValidVO> ticketValidPage(Page<com.mei.hui.miner.feign.vo.SwarmTicketValidVO> swarmTicketValidVOPage, Long ticketValid);
}
