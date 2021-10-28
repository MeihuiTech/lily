package com.mei.hui.browser.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel
@Data
public class PowerRankingVO {

    @ApiModelProperty("排序号")
    private long sort;

    @ApiModelProperty("存储id")
    private String MinerId;

    @ApiModelProperty("存储有效算力,单位B")
    private BigDecimal minerPowerAvailable;

    @ApiModelProperty("全网有效算力,单位B")
    private BigDecimal totalPowerAvailable;

    @ApiModelProperty("24小时出块奖励,单位attoFIL")
    private BigDecimal twentyFourBlockAward;

    @ApiModelProperty("24小时算力增长,单位B")
    private BigDecimal twentyFourPower;


}
