package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilBillBalanceDayAgg;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/9/16 11:04
 **/
public interface FilBillBalanceDayAggService extends IService<FilBillBalanceDayAgg> {


    /**
     * 根据minerId、date查询矿工总余额表，只返回一条数据
     * @param minerId
     * @param date
     * @return
     */
    public FilBillBalanceDayAgg selectFilBillBalanceDayAggByMinerIdAndDate(String minerId,LocalDate date);

    /**
     * 根据minerId、date、balance插入矿工总余额表
     * @param minerId
     * @param date
     * @param balance
     * @return
     */
    public Integer insertFilBillBalanceDayAgg(String minerId, LocalDate date, BigDecimal balance);

    /**
     * 根据id、balance修改矿工总余额表
     * @param id
     * @param balance
     * @return
     */
    public Integer updateFilBillBalanceDayAgg(Long id, BigDecimal balance);

}
