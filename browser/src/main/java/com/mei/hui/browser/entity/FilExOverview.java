package com.mei.hui.browser.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/18 20:01
 **/
@Data
@TableName("fil_ex_overview")
public class FilExOverview {

    /**
     * 主键
     */
    private Long id;

    /**
     * 区块高度
     */
    private Long height;

    /**
     * filecoin时间戳
     */
    private Long timestamp;

    /**
     * 全网有效算力(bytes)
     */
    private BigDecimal totalQaBytesPower;

    /**
     * 近24小时增长算力(bytes)
     */
    private BigDecimal totalQaBytesPowerIncrease24h;

    /**
     * 32GB扇区gas消耗(Fil/TiB)
     */
    private BigDecimal thirtyTwoGas;

    /**
     * 64GB扇区gas消耗(Fil/TiB)
     */
    private BigDecimal sixtyFourGas;

    /**
     * 当前扇区质押量(Fil/TiB)
     */
    private BigDecimal sectorPledge;

    /**
     * 近24小时产出效率(Fil/TiB)
     */
    private BigDecimal outputEfficiency24h;

    /**
     * 当前基础费率(autoFil)
     */
    private BigDecimal baseFee;

    /**
     * 每区块奖励(autoFil)
     */
    private BigDecimal blockReward;

    /**
     * 全网出块奖励(autoFil)
     */
    private BigDecimal minedFil;

    /**
     * 销毁量(autoFil)
     */
    private BigDecimal burntFil;

    /**
     * 活跃节点
     */
    private Long participatingMinerCount;

    /**
     * 全网出块数量
     */
    private Long blockCount;

    private Long createdAt;

    private Long updatedAt;

}
