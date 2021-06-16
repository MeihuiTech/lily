package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.miner.entity.SwarmAgg;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface SwarmAggMapper extends BaseMapper<SwarmAgg> {

    /**
     * 根据userId、昨天开始时间、昨天结束时间 在聚合统计表里获取昨天的总有效出票数
     * @param userId
     * @param beginYesterdayDate
     * @param endYesterdayDate
     * @return
     */
    public Long selectYesterdayTicketValid(@Param("userId") Long userId, @Param("beginYesterdayDate") Date beginYesterdayDate, @Param("endYesterdayDate") Date endYesterdayDate);
}