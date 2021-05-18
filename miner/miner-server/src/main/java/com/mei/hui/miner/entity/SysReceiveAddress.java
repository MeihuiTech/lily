package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 收款地址表
 */
@Data
@TableName("sys_receive_address")
public class SysReceiveAddress {
    private Long id;

    private Long userId;

    private Long currencyId;

    private String receiveAddr;

    private Boolean delFlag;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String remark;

}