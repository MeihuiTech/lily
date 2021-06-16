package com.mei.hui.miner.service.impl;

import com.mei.hui.miner.entity.SwarmAgg;
import com.mei.hui.miner.mapper.SwarmAggMapper;
import com.mei.hui.miner.service.ISwarmAggService;
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
public class SwarmAggServiceImpl implements ISwarmAggService {

    @Autowired
    private SwarmAggMapper swarmAggMapper;

    /**
     * 根据userId、昨天开始时间、昨天结束时间 在聚合统计表里获取昨天的总有效出票数
     * @param userId
     * @param beginYesterdayDate
     * @param endYesterdayDate
     * @return
     */
    @Override
    public Long selectYesterdayTicketValid(Long userId, Date beginYesterdayDate, Date endYesterdayDate) {
        return swarmAggMapper.selectYesterdayTicketValid(userId, beginYesterdayDate, endYesterdayDate);
    }
}
