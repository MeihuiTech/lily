package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.FilBillBalanceDayAgg;
import com.mei.hui.miner.mapper.FilBillBalanceDayAggMapper;
import com.mei.hui.miner.service.FilBillBalanceDayAggService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/9/16 11:05
 **/
@Service
@Slf4j
public class FilBillBalanceDayAggServiceImpl extends ServiceImpl<FilBillBalanceDayAggMapper,FilBillBalanceDayAgg> implements FilBillBalanceDayAggService {

    @Autowired
    private FilBillBalanceDayAggMapper filBillBalanceDayAggMapper;

    /*根据minerId、date查询矿工总余额表，只返回一条数据*/
    @Override
    public FilBillBalanceDayAgg selectFilBillBalanceDayAggByMinerIdAndDate(String minerId,LocalDate date){
        QueryWrapper<FilBillBalanceDayAgg> queryWrapper = new QueryWrapper<>();
        FilBillBalanceDayAgg filBillBalanceDayAgg = new FilBillBalanceDayAgg();
        filBillBalanceDayAgg.setMinerId(minerId);
        filBillBalanceDayAgg.setDate(date);
        queryWrapper.setEntity(filBillBalanceDayAgg);
        FilBillBalanceDayAgg dbFilBillBalanceDayAgg = this.getOne(queryWrapper);
        return dbFilBillBalanceDayAgg;
    }

    /*根据minerId、date、balance插入矿工总余额表*/
    @Override
    public Integer insertFilBillBalanceDayAgg(String minerId, LocalDate date, BigDecimal balance){
        FilBillBalanceDayAgg filBillBalanceDayAgg = new FilBillBalanceDayAgg();
        filBillBalanceDayAgg.setMinerId(minerId);
        filBillBalanceDayAgg.setDate(date);
        filBillBalanceDayAgg.setBalance(balance);
        filBillBalanceDayAgg.setCreateTime(LocalDateTime.now());
        Integer count  = filBillBalanceDayAggMapper.insert(filBillBalanceDayAgg);
        return count;
    }

    /*根据id、balance修改矿工总余额表*/
    @Override
    public Integer updateFilBillBalanceDayAgg(Long id, BigDecimal balance){
        FilBillBalanceDayAgg filBillBalanceDayAgg = new FilBillBalanceDayAgg();
        filBillBalanceDayAgg.setId(id);
        filBillBalanceDayAgg.setBalance(balance);
        filBillBalanceDayAgg.setUpdateTime(LocalDateTime.now());
        Integer count  = filBillBalanceDayAggMapper.updateById(filBillBalanceDayAgg);
        return count;
    }

}
