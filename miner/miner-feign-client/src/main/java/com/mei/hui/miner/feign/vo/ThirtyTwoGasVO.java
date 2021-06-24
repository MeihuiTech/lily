package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel("32G扇区gas封装成本")
public class ThirtyTwoGasVO {

    @ApiModelProperty("gas费用")
    private BigDecimal gas;

    @ApiModelProperty("总成本")
    private BigDecimal cost;

    @ApiModelProperty("质押费")
    private BigDecimal pledge;

}
