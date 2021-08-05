package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/5 11:59
 **/
@Data
@ApiModel(value = "子账户")
public class FilBillSubAccountVO {

    @ApiModelProperty(value = "子账户名称")
    private String name;

    @ApiModelProperty(value = "子账户地址")
    private String address;

}
