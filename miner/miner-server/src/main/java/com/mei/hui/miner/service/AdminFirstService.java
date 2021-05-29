package com.mei.hui.miner.service;

import com.mei.hui.util.Result;

import java.math.BigDecimal;

public interface AdminFirstService {


    /**
    * 管理员首页-旷工统计数据-当天出块份数
    *
    * @description
    * @author shangbin
    * @date 2021/5/28 15:37
    * @param [yesterDay]
    * @return void
    * @version v1.0.0
    */
    public Long selectAllBlocksPerDay();

    /**
    * 管理员首页-旷工统计数据-平台总资产
    *
    * @description
    * @author shangbin
    * @date 2021/5/28 17:04
    * @param []
    * @return java.math.BigDecimal
    * @version v1.0.0
    */
    public BigDecimal selectAllBalanceMinerAccount();

    /**
    * 管理员首页-旷工统计数据-平台有效算力
    *
    * @description
    * @author shangbin
    * @date 2021/5/28 17:16
    * @param []
    * @return java.math.BigDecimal
    * @version v1.0.0
    */
    public BigDecimal selectAllPowerAvailable();

    /**
    * 管理员首页-旷工统计数据-活跃旷工
    *
    * @description
    * @author shangbin
    * @date 2021/5/28 17:22
    * @param []
    * @return java.lang.Long
    * @version v1.0.0
    */
    public Long selectAllMinerIdCount();
}
