package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sys_miner_info")
public class SysMinerInfo
{
    @TableId(type= IdType.AUTO)
    private Long id;
    private Long userId;
    private String minerId;
    private BigDecimal powerAvailable;
    private BigDecimal sectorPledge;
    private Long sectorSize;
    private Integer sectorProving;
    private Integer sectorAvailable;

    private Integer sectorError;

    //窗口序号
    private Long deadlineIndex;
    //窗口扇区数
    private Long deadlineSectors;
    //窗口运行时间
    private Long provingPeriodStart;

    //证明高度
    private Long provingPeriodEpoch;

    //当前高度
    private Long currentEpoch;

    /**
     * 锁仓收益, 单位FIL
     */
    private BigDecimal lockAward;

    /**
     * 累计出块奖励,单位FIL
     */
    private BigDecimal totalBlockAward;

    /**
     * 矿工可用余额,单位FIL
     */
    private BigDecimal balanceMinerAvailable;

    /**
     * 挖矿账户余额, 单位FIL
     */
    private BigDecimal balanceMinerAccount;

    private Long blocksPerDay;

    private Long totalBlocks;

    private BigDecimal powerIncreasePerDay;

    private String createBy;

    private String updateBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Long workerCount = 0L;

    @TableField(exist = false)
    private Long machineCount = 0L;

    @TableField(exist = false)
    private long pageNum = 1;

    @TableField(exist = false)
    private long pageSize = 10;
}
