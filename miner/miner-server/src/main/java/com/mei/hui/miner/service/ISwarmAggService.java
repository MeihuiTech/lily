package com.mei.hui.miner.service;

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
}
