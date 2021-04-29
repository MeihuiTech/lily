package com.mei.hui.miner.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 算力按天聚合对象 sys_agg_power_daily
 * @author ruoyi
 * @date 2021-04-06
 */
@Data
public class SysAggPowerDaily{

    /** $column.columnComment */
    private Long id;

    /** 矿工id */
    private String minerId;

    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String date;

    /** 有效算力 */
    private Long powerAvailable;

    /** 算力增长 */
    private Long powerIncrease;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String createBy;

    private String updateBy;



}
