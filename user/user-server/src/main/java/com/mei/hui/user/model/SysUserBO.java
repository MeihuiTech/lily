package com.mei.hui.user.model;

import com.mei.hui.user.entity.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/5/22 15:33
 **/
@Data
@Api("修改用户基本信息入参")
public class SysUserBO extends SysUser {


    @ApiModelProperty(value = "短信验证码",required = true)
    private String smsCode;

    @ApiModelProperty(value = "业务名称,输入字符串",required = true)
    private String serviceName;


}
