package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class AddCurrencyBO {

    @ApiModelProperty(value = "币种名字",required = true)
    private String name;

    @ApiModelProperty(value = "区块链名字",required = true)
    private String type;

    @ApiModelProperty(value = "more费率",required = true)
    private BigDecimal rate;

}
