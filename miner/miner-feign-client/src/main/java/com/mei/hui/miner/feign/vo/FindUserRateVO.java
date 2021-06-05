package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
@ApiModel
public class FindUserRateVO {

    @ApiModelProperty(value = "用户id",required = true)
    private Long userId;

    @ApiModelProperty(value = "币种：FIL,CHIA",required = true)
    private String type;

    @ApiModelProperty(value = "币种费率",required = true)
    private BigDecimal feeRate;
}
