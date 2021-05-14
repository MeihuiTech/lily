package com.mei.hui.miner.model;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shangbin
 * @version v1.0.0
 * @date $ $
 **/
@Api("收款地址入参")
@Data
public class SysReceiveAddressBO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "币种表id")
    private Long currencyId;

    @ApiModelProperty(value = "收款地址")
    private String receiveAddr;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "验证码")
    private String verifyCode;

}
