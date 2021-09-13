package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@ApiModel
@Data
@Accessors(chain = true)
public class NoPlatformVOPage {

    @ApiModelProperty("矿工id")
    private String minerId;

    @ApiModelProperty("0-未上报;1-已上报")
    private int type;

    @ApiModelProperty("设备数")
    private int deviceNum;
}
