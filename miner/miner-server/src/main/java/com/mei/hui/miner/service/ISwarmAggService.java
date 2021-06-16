package com.mei.hui.miner.service;

import java.util.Date;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.SwarmAgg;
import com.mei.hui.miner.feign.vo.SwarmHomePageVO;
import com.mei.hui.util.Result;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 14:15
 **/
public interface ISwarmAggService extends IService<SwarmAgg> {
    /**
     *
     * swarm普通用户首页聚合数据
     * @return
     */
    Result<SwarmHomePageVO> homePage();


    /**
    * 根据userId、昨天开始时间、昨天结束时间 在聚合统计表里获取昨天的总有效出票数
    *
    * @description
    * @author shangbin
    * @date 2021/6/16 19:20
    * @param [userId, beginYesterdayDate, endYesterdayDate]
    * @return java.lang.Long
    * @version v1.0.0
    */
    public Long selectYesterdayTicketValid(Long userId, Date beginYesterdayDate, Date endYesterdayDate);
}
