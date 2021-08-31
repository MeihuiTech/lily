package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.SysAggPowerHour;
import com.mei.hui.miner.mapper.SysAggPowerHourMapper;
import com.mei.hui.miner.service.ISysAggPowerHourService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/30 16:39
 **/
@Service
@Slf4j
public class SysAggPowerHourServiceImpl extends ServiceImpl<SysAggPowerHourMapper,SysAggPowerHour> implements ISysAggPowerHourService {

    @Autowired
    private SysAggPowerHourMapper sysAggPowerHourMapper;

    /*根据minerId、date查询算力按小时聚合表list*/
    @Override
    public List<SysAggPowerHour> selectSysAggPowerHourByMinerIdDate(String type,String minerId, LocalDateTime date) {
        QueryWrapper<SysAggPowerHour> sysAggPowerHourQueryWrapper = new QueryWrapper<>();
        SysAggPowerHour dbSysAggPowerHour = new SysAggPowerHour();
        dbSysAggPowerHour.setMinerId(minerId);
        dbSysAggPowerHour.setDate(date);
        dbSysAggPowerHour.setType(type);
        sysAggPowerHourQueryWrapper.setEntity(dbSysAggPowerHour);
        List<SysAggPowerHour> dbSysAggPowerHourList = sysAggPowerHourMapper.selectList(sysAggPowerHourQueryWrapper);
        return dbSysAggPowerHourList;
    }

    /*根据minerId、date查询算力按小时聚合表list*/
    @Override
    public List<SysAggPowerHour> selectLastSysAggPowerHourByMinerIdDate(String type,String minerId,String startDate,String endDate) {
        List<SysAggPowerHour> dbSysAggPowerHourList = sysAggPowerHourMapper.selectLastSysAggPowerHourByMinerIdDate(type,minerId,startDate,endDate);
        return dbSysAggPowerHourList;
    }

    /*查询FIL币算力按小时聚合表里近24小时所有的每小时出块份数总和*/
    @Override
    public Long selectTwentyFourTotalBlocks(String type, String minerId, String startDate, String endDate) {
        return sysAggPowerHourMapper.selectTwentyFourTotalBlocks(type,  minerId,  startDate,  endDate);
    }

    /*查询FIL币算力按小时聚合表里近24小时所有的每小时算力增长总和*/
    @Override
    public BigDecimal selectTwentyFourPowerIncrease(String type, String minerId, String startDate, String endDate) {
        return sysAggPowerHourMapper.selectTwentyFourPowerIncrease(type,  minerId,  startDate,  endDate);
    }

    /*查询FIL币算力按小时聚合表里近24小时所有的每小时新增出块奖励总和*/
    @Override
    public BigDecimal selectTwentyFourTotalBlockAward(String type, String minerId, String startDate, String endDate) {
        return sysAggPowerHourMapper.selectTwentyFourTotalBlockAward(type,  minerId,  startDate,  endDate);
    }


}
