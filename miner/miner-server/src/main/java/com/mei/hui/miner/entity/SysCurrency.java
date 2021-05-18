package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 币种表
 */
@Data
@TableName("sys_currency")
public class SysCurrency {
    private Long id;

    private String name;

    private String picAddress;

    private Integer orderNum;

    private Boolean delFlag;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

}