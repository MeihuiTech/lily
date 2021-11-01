package com.mei.hui.browser.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class PowerIncrRankVO {

    @ApiModelProperty("排序号")
    private long sort;

    @ApiModelProperty("存储id")
    private String MinerId;

    @ApiModelProperty("存储有效算力,单位B")
    private BigDecimal minerPowerAvailable;

    @ApiModelProperty("24小时算力增长,单位B")
    private BigDecimal twentyFourPower;
}
