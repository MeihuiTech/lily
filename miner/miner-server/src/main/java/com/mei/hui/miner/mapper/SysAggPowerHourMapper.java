package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.miner.entity.SysAggPowerHour;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/30 19:14
 **/
@Repository
public interface SysAggPowerHourMapper extends BaseMapper<SysAggPowerHour> {

    /**
     * 查询FIL币算力按小时聚合表里近24小时所有的每小时出块份数总和
     *
     * @param type
     * @param minerId
     * @param startDate
     * @param endDate
     * @return
     */
    public Long selectTwentyFourTotalBlocks(@Param("type") String type, @Param("minerId") String minerId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询FIL币算力按小时聚合表里近24小时所有的每小时算力增长总和
     *
     * @param type
     * @param minerId
     * @param startDate
     * @param endDate
     * @return
     */
    public BigDecimal selectTwentyFourPowerIncrease(@Param("type") String type, @Param("minerId") String minerId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询FIL币算力按小时聚合表里近24小时所有的每小时新增出块奖励总和
     *
     * @param type
     * @param minerId
     * @param startDate
     * @param endDate
     * @return
     */
    public BigDecimal selectTwentyFourTotalBlockAward(@Param("type") String type, @Param("minerId") String minerId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询近24小时内上一个小时：根据minerId、date查询算力按小时聚合表list
     *
     * @param type
     * @param minerId
     * @param startDate
     * @param endDate
     * @return
     */
    public List<SysAggPowerHour> selectLastSysAggPowerHourByMinerIdDate(@Param("type") String type, @Param("minerId") String minerId, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
