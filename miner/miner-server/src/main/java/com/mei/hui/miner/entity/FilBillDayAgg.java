package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
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
    private LocalDateTime date;

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


    private LocalDateTime createTime;


    private LocalDateTime updateTime;
}
