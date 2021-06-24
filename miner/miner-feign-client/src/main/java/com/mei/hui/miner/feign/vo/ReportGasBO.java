package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class ReportGasBO {

    @ApiModelProperty(value = "32G矿工Gas费用",required = true)
    private BigDecimal thirtyTwoGas;

    @ApiModelProperty(value = "32G矿工总成本",required = true)
    private BigDecimal thirtyTwoCost;

    @ApiModelProperty(value = "32G矿工质押费用",required = true)
    private BigDecimal thirtyTwoPledge;

    @ApiModelProperty(value = "64G矿工Gas费用",required = true)
    private BigDecimal sixtyFourGas;

    @ApiModelProperty(value = "64G矿工总成本",required = true)
    private BigDecimal sixtyFourCost;

    @ApiModelProperty(value = "64G矿工质押费用",required = true)
    private BigDecimal sixtyFourPledge;
}
