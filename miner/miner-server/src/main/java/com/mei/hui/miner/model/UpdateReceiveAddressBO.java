package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UpdateReceiveAddressBO {

    @ApiModelProperty(value = "id",required = true)
    private Long id;

    @ApiModelProperty(value = "币种表id",required = true)
    private Integer currencyId;

    @ApiModelProperty(value = "收款地址",required = true)
    private String address;

//    @ApiModelProperty(value = "备注")
//    private String remark;

    @ApiModelProperty(value = "短信验证码",required = true)
    private String smsCode;

    @ApiModelProperty(value = "业务名称,输入字符串",required = true)
    private String serviceName;

}
