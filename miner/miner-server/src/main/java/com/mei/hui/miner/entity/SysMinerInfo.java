package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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

    private BigDecimal lockAward;

    private BigDecimal totalBlockAward;

    private BigDecimal balanceMinerAvailable;

    private BigDecimal balanceMinerAccount;

    private Long blocksPerDay;

    private Long totalBlocks;

    private BigDecimal powerIncreasePerDay;

    private String createBy;

    private String updateBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long workerCount = 0L;
    private Long machineCount = 0L;
}
