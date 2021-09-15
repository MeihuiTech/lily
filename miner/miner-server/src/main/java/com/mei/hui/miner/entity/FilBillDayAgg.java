package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * FIL币账单消息每天汇总表
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/17 17:04
 **/
@Data
@TableName("fil_bill_day_agg")
public class FilBillDayAgg {

    @TableId(type = IdType.AUTO)
    private Integer id;


    private String minerId;

    /**
     * 日期
     */
    private LocalDate date;

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

    /**
     * 类型：默认0正常，1补录
     */
    private Integer type;

    private LocalDateTime createTime;


    private LocalDateTime updateTime;
}
