package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户提现聚合表
 */
@Data
@Builder
@TableName("mr_agg_withdraw")
public class MrAggWithdraw {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sysUserId;

    private BigDecimal takeTotalMony;

    private BigDecimal totalFee;

    private Integer tatalCount;
}
