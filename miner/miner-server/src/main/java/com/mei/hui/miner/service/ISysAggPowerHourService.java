package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.SysAggPowerHour;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/30 16:38
 **/
public interface ISysAggPowerHourService extends IService<SysAggPowerHour> {

    /**
     * 根据minerId、date查询算力按小时聚合表list
     * @param minerId
     * @param date
     * @return
     */
    public List<SysAggPowerHour> selectSysAggPowerHourByMinerIdDate(String type,String minerId,LocalDateTime date);

}
