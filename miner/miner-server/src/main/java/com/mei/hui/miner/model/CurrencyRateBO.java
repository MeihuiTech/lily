package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CurrencyRateBO {

    @ApiModelProperty(value = "货比种类,fil币-FIL,起亚币-CHIA",required = true)
    private String type;

    @ApiModelProperty(value = "费率",required = true)
    private double feeRate;
}
