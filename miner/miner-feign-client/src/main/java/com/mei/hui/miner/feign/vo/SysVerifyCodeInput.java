package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel
public class SysVerifyCodeInput implements Serializable {

    private Long id;

    /** 用户ID */
    private Long userId;

    /** 手机号 */
    private String phone;

    /** 验证码 */
    private String verifyCode;

    /** 0 未使用 1 已使用 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
