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
     * 扇区总数
     */
    private Integer sectorTotal;

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

    /**
     * 所有在线矿机数量
     */
    @TableField(exist = false)
    private Integer allOnlineMachineCount;

    /**
     * 所有离线矿机数量
     */
    @TableField(exist = false)
    private Integer allOfflineMachineCount;

    /**
     * Miner在线矿机数量
     */
    @TableField(exist = false)
    private Integer minerOnlineMachineCount;

    /**
     * Miner离线矿机数量
     */
    @TableField(exist = false)
    private Integer minerOfflineMachineCount;

    /**
     * post在线矿机数量
     */
    @TableField(exist = false)
    private Integer postOnlineMachineCount;

    /**
     * post离线矿机数量
     */
    @TableField(exist = false)
    private Integer postOfflineMachineCount;

    /**
     * c2在线矿机数量
     */
    @TableField(exist = false)
    private Integer ctwoOnlineMachineCount;

    /**
     * c2离线矿机数量
     */
    @TableField(exist = false)
    private Integer ctwoOfflineMachineCount;

    /**
     * seal在线矿机数量
     */
    @TableField(exist = false)
    private Integer sealOnlineMachineCount;

    /**
     * seal离线矿机数量
     */
    @TableField(exist = false)
    private Integer sealOfflineMachineCount;


}
