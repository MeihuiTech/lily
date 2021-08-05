package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fil_miner_control_balance")
public class FilMinerControlBalance {
    @TableId(type= IdType.AUTO)
    private Long id;

    private String minerId;

    /**
     * Post账户名称
     */
    private String name;

    /**
     * Post账户余额
     */
    private BigDecimal balance;

    /**
     * Post账户地址
     */
    private String address;

    private LocalDateTime createTime;

}