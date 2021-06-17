package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.SwarmNode;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;

import java.util.Date;
import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 14:15
 **/
public interface ISwarmNodeService extends IService<SwarmNode> {

    /**
     * 节点分页列表
     * @param bo
     * @return
     */
    PageResult<NodePageListVO> nodePageList(NodePageListBO bo);

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

    /**
    * 管理员-用户收益-多条件分页查询用户列表
    *
    * @description
    * @author shangbin
    * @date 2021/6/17 14:02
    * @param [swarmUserMoneyBO]
    * @return com.mei.hui.util.Result<java.util.List<com.mei.hui.miner.feign.vo.SwarmUserMoneyVO>>
    * @version v1.0.0
    */
    public PageResult<SwarmUserMoneyVO> selectUserMoneyList(SwarmUserMoneyBO swarmUserMoneyBO);

    /**
     * 获取节点ip列表
     * @return
     */
    Result<List<FindNodeListVO>> findNodeList();
}
