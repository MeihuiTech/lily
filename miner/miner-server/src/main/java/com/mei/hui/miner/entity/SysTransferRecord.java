package com.mei.hui.miner.entity;

import com.mei.hui.util.BasePage;
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
public class SysTransferRecord extends BasePage
{
    /** $column.columnComment */
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

    /** 0 提币中 1 提币完成 2 提币失败 */
    private Integer status;

    private String createBy;

    private String updateBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
