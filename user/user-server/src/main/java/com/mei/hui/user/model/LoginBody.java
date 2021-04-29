package com.mei.hui.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户登录对象
 * 
 * @author ruoyi
 */
@ApiModel
@Data
public class LoginBody
{
    @ApiModelProperty(value = "用户名",required = true)
    private String username;

    @ApiModelProperty(value = "用户密码",required = true)
    private String password;

    @ApiModelProperty(value = "验证码",required = true)
    private String code;

    @ApiModelProperty(value = "唯一标识 uuid",required = true)
    private String uuid = "";

}
