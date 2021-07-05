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

    /**
     * 有效算力, 单位B
     */
    private BigDecimal powerAvailable;

    /**
     * 扇区质押, 单位FIL
     */
    private BigDecimal sectorPledge;

    /**
     * 扇区大小,单位GB
     */
    private Long sectorSize;

    /**
     * 证明状态扇区数量
     */
    private Integer sectorProving;

    /**
     * 有效状态扇区数量
     */
    private Integer sectorAvailable;

    /**
     * 错误状态扇区数量
     */
    private Integer sectorError;

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

    /**
     * worker账户余额
     */
    private BigDecimal balanceWorkerAccount;

    /**
     * 当天出块份数
     */
    @TableField(exist = false)
    private Long blocksPerDay;

    /**
     * 累计出块份数
     */
    private Long totalBlocks;

    /**
     * 算力增速
     */
    @TableField(exist = false)
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

    @TableField(exist = false)
    private BigDecimal postBalance;

}
