package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * FIL币账单参数表
 */
@Data
@TableName("fil_bill_params")
public class FilBillParams {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * FIL币账单表id
     */
    private Long filBillId;

    /**
     * 参数
     */
    private String params;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}