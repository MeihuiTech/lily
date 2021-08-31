package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.SysAggPowerHour;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/30 16:38
 **/
public interface ISysAggPowerHourService extends IService<SysAggPowerHour> {

    /**
     * 查询近24小时内上一个小时：根据minerId、date查询算力按小时聚合表list
     * @param minerId
     * @param date
     * @return
     */
    public List<SysAggPowerHour> selectLastSysAggPowerHourByMinerIdDate(String type,String minerId,String startDate,String endDate);

    /**
     * 查询FIL币算力按小时聚合表里近24小时所有的每小时出块份数总和
     * @param type
     * @param minerId
     * @param startDate
     * @param endDate
     * @return
     */
    public Long selectTwentyFourTotalBlocks(String type, String minerId, String startDate, String endDate);

    /**
     * 查询FIL币算力按小时聚合表里近24小时所有的每小时算力增长总和
     * @param type
     * @param minerId
     * @param startDate
     * @param endDate
     * @return
     */
    public BigDecimal selectTwentyFourPowerIncrease(String type, String minerId, String startDate, String endDate);

    /**
     * 查询FIL币算力按小时聚合表里近24小时所有的每小时新增出块奖励总和
     * @param type
     * @param minerId
     * @param startDate
     * @param endDate
     * @return
     */
    public BigDecimal selectTwentyFourTotalBlockAward(String type, String minerId, String startDate, String endDate);

    /**
     * 根据minerId、date查询算力按小时聚合表list
     * @param name
     * @param minerId
     * @param date
     * @return
     */
    public List<SysAggPowerHour> selectSysAggPowerHourByMinerIdDate(String name, String minerId, LocalDateTime date);
}
