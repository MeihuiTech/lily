package com.mei.hui.miner.service;

import com.mei.hui.miner.entity.ChiaMiner;
import com.mei.hui.miner.model.SysMinerInfoBO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 起亚币旷工信息表
 */
public interface IChiaMinerService {


    Map<String,Object> findChiaMinerPage(SysMinerInfoBO sysMinerInfoBO);


    /**
     * chia管理员首页-旷工统计数据-平台总资产
     *
     * @description
     * @author shangbin
     * @date 2021/5/28 17:04
     * @param []
     * @return java.math.BigDecimal
     * @version v1.0.0
     */
    public BigDecimal selectFilAllBalanceMinerAccount();

    /**
     * chia管理员首页-旷工统计数据-平台有效算力
     *
     * @description
     * @author shangbin
     * @date 2021/5/28 17:16
     * @param []
     * @return java.math.BigDecimal
     * @version v1.0.0
     */
    public BigDecimal selectFilAllPowerAvailable();

    /**
     * chia管理员首页-旷工统计数据-活跃旷工
     *
     * @description
     * @author shangbin
     * @date 2021/5/28 17:22
     * @param []
     * @return java.lang.Long
     * @version v1.0.0
     */
    public Long selectFilAllMinerIdCount();

    /**
     * chia管理员首页-旷工统计数据-当天出块份数
     *
     * @description
     * @author shangbin
     * @date 2021/5/28 15:37
     * @param [yesterDay]
     * @return void
     * @version v1.0.0
     */
    public Long selectFilAllBlocksPerDay(String yesterDayDate);

    /**
    * 根据用户id、旷工id查询起亚币旷工信息表中是否有数据
    *
    * @description
    * @author shangbin
    * @date 2021/6/1 17:26
    * @param [userId, minerId]
    * @return com.mei.hui.miner.entity.ChiaMiner
    * @version v1.0.0
    */
    public List<ChiaMiner> selectChiaMinerByUserIdAndMinerId(Long userId, String minerId);

    /**
    * 新增起亚币旷工信息表
    *
    * @description
    * @author shangbin
    * @date 2021/6/1 17:32
    * @param [chiaMiner]
    * @return int
    * @version v1.0.0
    */
    public int insertChiaMiner(ChiaMiner chiaMiner);

    /**
    * 修改起亚币旷工信息表
    *
    * @description
    * @author shangbin
    * @date 2021/6/1 17:33
    * @param [chiaMiner]
    * @return int
    * @version v1.0.0
    */
    public int updateChiaMiner(ChiaMiner chiaMiner);
}
