package com.mei.hui.miner.feign.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * FIL币账单消息每天汇总表入参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/9/9 19:23
 **/
@Data
public class FilBillDayAggArgsVO {


    /**
     * 收入
     */
    private BigDecimal inMoney;

    /**
     * 支出
     */
    private BigDecimal outMoney;

    /**
     * 结余
     */
    private BigDecimal balance;

    /**
     * 收入-转账
     */
    private BigDecimal inTransfer;

    /**
     * 收入-区块奖励
     */
    private BigDecimal inBlockAward;

    /**
     * 支出-转账
     */
    private BigDecimal outTransfer;

    /**
     * 支出-矿工手续费
     */
    private BigDecimal outNodeFee;

    /**
     * 支出-燃烧手续费
     */
    private BigDecimal outBurnFee;

    /**
     * 支出-其它
     */
    private BigDecimal outOther;

}
