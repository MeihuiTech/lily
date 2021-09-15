package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class NoPlatformAddBO {

    @ApiModelProperty(value = "矿工id",required = true)
    private String minerId;

    @ApiModelProperty(value = "设备数",required = false)
    private int deviceNum;

}
