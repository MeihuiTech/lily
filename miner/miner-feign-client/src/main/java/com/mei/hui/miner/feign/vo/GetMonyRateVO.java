package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class GetMonyRateVO {
    @ApiModelProperty(value = "货比种类,XCH,BZZ,FIL")
    private String type;

    @ApiModelProperty(value = "资产占比")
    private BigDecimal rate;
}
