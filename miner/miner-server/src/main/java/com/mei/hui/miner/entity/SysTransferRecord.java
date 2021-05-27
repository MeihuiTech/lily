package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 系统划转记录对象 sys_transfer_record
 * 
 * @author ruoyi
 * @date 2021-03-08
 */
@Data
@TableName("sys_transfer_record")
public class SysTransferRecord
{
    /** $column.columnComment */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 提取金额 */
    private BigDecimal amount;

    /** 平台收取手续费 */
    private BigDecimal fee;

    /** 手续费HASH */
    private String feeHash;

    /** 提币地址 */
    private String toAddress;

    /** 提币HASH */
    private String toHash;

    private String minerId;

    /** 0 提币中 1 提币完成 2 提币失败 */
    private Integer status;
    @TableField(exist = false)
    private String createBy;
    @TableField(exist = false)
    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private long pageNum = 1;
    @TableField(exist = false)
    private long pageSize = 10;
    @TableField(exist = false)
    private String userName;

    private String remark;

    private String type;

    @TableField(exist = false)
    private boolean isAsc;

    @TableField(exist = false)
    private String cloumName;

}
