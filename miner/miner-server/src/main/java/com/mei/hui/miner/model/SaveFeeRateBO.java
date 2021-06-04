package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel
public class SaveFeeRateBO {

    @ApiModelProperty(value = "用户id",required = true)
    private Long userId;

    @ApiModelProperty(value = "币种费率集合",required = true)
    List<CurrencyRateBO> rats = new ArrayList<>();
}
