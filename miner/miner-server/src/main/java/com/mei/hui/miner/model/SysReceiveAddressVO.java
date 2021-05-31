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
@ApiModel("收款地址出参")
@Data
public class SysReceiveAddressVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "币种表id")
    private Long currencyId;

    @ApiModelProperty(value = "收款地址")
    private String address;

//    @ApiModelProperty(value = "备注")
//    private String remark;


}
