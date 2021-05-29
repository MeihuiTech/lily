package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 算力按天聚合对象 sys_agg_power_daily
 * @author ruoyi
 * @date 2021-04-06
 */
@Data
@TableName("sys_agg_power_daily")
public class SysAggPowerDaily{

    /** $column.columnComment */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 矿工id */
    private String minerId;

    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String date;

    /** 有效算力 */
    private BigDecimal powerAvailable;

    /** 算力增长 */
    private BigDecimal powerIncrease;

    private String type;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String createBy;

    private String updateBy;

    private Long totalBlocks;

    private BigDecimal blockAwardIncrease;

}
