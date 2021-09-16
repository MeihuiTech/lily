package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
* 矿工总余额表
*
* @description
* @author shangbin
* @date 2021/9/16 10:57
* @version v1.4.1
*/
@Data
@TableName("fil_bill_balance_day_agg")
public class FilBillBalanceDayAgg {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 矿工id */
    private String minerId;

    /** 日期 */
    private LocalDate date;

    /** 总结余，包括miner、worker、controller子账户的 */
    private BigDecimal balance;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
