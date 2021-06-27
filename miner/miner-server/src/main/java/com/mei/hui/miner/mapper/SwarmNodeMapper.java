package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.miner.entity.SwarmNode;
import com.mei.hui.miner.entity.TotalMoneyAndTicketNum;
import com.mei.hui.miner.feign.vo.SwarmTicketValidVO;
import com.mei.hui.miner.feign.vo.SwarmUserMoneyBO;
import com.mei.hui.miner.feign.vo.SwarmUserMoneyVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface SwarmNodeMapper extends BaseMapper<SwarmNode> {


    /**
    * 管理员首页-平台概览-总有效出票数，用的字段：有效出票数
    *
    * @description
    * @author shangbin
    * @date 2021/6/16 15:23
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
     * @date 2021/6/16 16:05
     * @param [beginYesterdayDate, endYesterdayDate]
     * @return java.lang.Long
     * @version v1.0.0
     */
    public Long selectYesterdayTicketValid(@Param("yesterDayDateYmd") String yesterDayDateYmd);

    /**
    * 管理员首页-平台概览-有效节点
    *
    * @description
    * @author shangbin
    * @date 2021/6/16 15:36
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
    * @date 2021/6/16 15:37
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
    * @date 2021/6/16 18:01
    * @param [swarmTicketValidVOPage, ticketValid]
    * @return com.baomidou.mybatisplus.core.metadata.IPage<com.mei.hui.miner.feign.vo.SwarmTicketValidVO>
    * @version v1.0.0
    */
    public IPage<SwarmTicketValidVO> ticketValidPage(Page<SwarmTicketValidVO> swarmTicketValidVOPage, @Param("ticketValid") Long ticketValid);

    /**
    * 管理员-用户收益-多条件分页查询用户列表
    *
    * @description
    * @author shangbin
    * @date 2021/6/17 14:22
    * @param [page, userId, userIdList]
    * @return com.baomidou.mybatisplus.core.metadata.IPage<com.mei.hui.miner.feign.vo.SwarmUserMoneyVO>
    * @version v1.0.0
    */
    IPage<SwarmUserMoneyVO> selectUserMoneyList(Page<SwarmUserMoneyVO> page,@Param("userId") Long userId,@Param("cloumName") String cloumName,
                                                @Param("isAsc") boolean isAsc, @Param("userIdList") List<Long> userIdList);

    TotalMoneyAndTicketNum getTotalMoneyAndTicketNum(Long userId);

    Map<String,Object> getYesterdayTicketAvailAndValid(Map<String,Object> map);
}