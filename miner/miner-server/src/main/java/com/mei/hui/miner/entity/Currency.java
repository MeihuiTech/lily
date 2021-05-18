package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 币种表
 */
@Data
@TableName("miner_currency")
public class Currency {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String imgUrl;

    private Integer sort;

    //1-可用;0-不可用
    private Integer status;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

}