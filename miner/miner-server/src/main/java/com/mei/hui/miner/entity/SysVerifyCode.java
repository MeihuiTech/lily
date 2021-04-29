package com.mei.hui.miner.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统验证码对象 sys_verify_code
 * 
 * @author ruoyi
 * @date 2021-03-08
 */
@Data
public class SysVerifyCode
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 手机号 */
    private String phone;

    /** 验证码 */
    private String verifyCode;

    /** 0 未使用 1 已使用 */
    private Integer status;

    private String createBy;

    private String updateBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
