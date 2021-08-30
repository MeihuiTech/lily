package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 算力按小时聚合表
 */
@Data
@TableName("sys_agg_power_hour")
public class SysAggPowerHour {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 矿工id */
    private String minerId;

    /** 时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    /**
     * 有效算力, 单位B
     */
    private BigDecimal powerAvailable;

    /**
     * 每小时算力增长, 单位B
     */
    private BigDecimal powerIncrease;

    /**
     * 累计出块奖励,单位FIL
     */
    private BigDecimal totalBlockAward;

    /**
     * 每小时新增出块奖励,单位FIL
     */
    private BigDecimal blockAwardIncrease;

    /**
     * 累计出块份数，fil币使用
     */
    private Long totalBlocks;

    /**
     * 每小时出块份数，fil币使用
     */
    private Long blocksPerDay;

    /**
     * 货币种类：XCH,BZZ,FIL
     */
    private String type;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
