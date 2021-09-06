package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@ApiModel
@Accessors(chain = true)
public class AvailablePowerVO {

    @ApiModelProperty("矿工id")
    private String minerId;

    @ApiModelProperty("有效算力, 单位B")
    private BigDecimal powerAvailable;
}
