package com.mei.hui.miner.model;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shangbin
 * @version v1.0.0
 * @date $ $
 **/
@ApiModel("收款地址入参")
@Data
public class SysReceiveAddressBO {

    @ApiModelProperty(value = "币种表id",required = true)
    private Long currencyId;

    @ApiModelProperty(value = "收款地址",required = true)
    private String address;

//    @ApiModelProperty(value = "备注")
//    private String remark;

    @ApiModelProperty(value = "短信验证码",required = true)
    private String smsCode;

    @ApiModelProperty(value = "业务名称,输入字符串：addReceiveAddress(\"新增收款地址\"),updateReceiveAddress(\"编辑收款地址\"),updateUser(\"修改用户信息\")",required = true)
    private String serviceName;


}
