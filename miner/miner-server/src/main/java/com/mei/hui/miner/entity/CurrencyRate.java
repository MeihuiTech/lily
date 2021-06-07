package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_currency_rate")
public class CurrencyRate {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String type;

    private BigDecimal feeRate;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
