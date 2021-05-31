package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("chia_miner")
public class ChiaMiner {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String minerId;

    //有效算力, 单位B
    private BigDecimal powerAvailable;

    //累计出块奖励,单位XCH
    private BigDecimal totalBlockAward;

    //总资产, 单位XCH
    private BigDecimal balanceMinerAccount;

    //累计出块份数
    private Long totalBlocks;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
