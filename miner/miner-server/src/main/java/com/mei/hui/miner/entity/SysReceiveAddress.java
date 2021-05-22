package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收款地址表
 */
@Data
@TableName("miner_receive_address")
public class SysReceiveAddress {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long currencyId;

    private String address;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}