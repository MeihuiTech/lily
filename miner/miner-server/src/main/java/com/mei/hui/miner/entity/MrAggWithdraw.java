package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 用户提现聚合表
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("mr_agg_withdraw")
public class MrAggWithdraw {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long sysUserId;

    /**
     * 用户提现总额
     */
    private BigDecimal takeTotalMony;

    /**
     * 手续费总额
     */
    private BigDecimal totalFee;

    /**
     * 提取次数
     */
    private Integer tatalCount;

    /**
     * 货币种类,FIL,CHIA
     */
    private String type;

}
