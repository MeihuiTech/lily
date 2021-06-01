package com.mei.hui.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ChangeCurrencyBO {

    @ApiModelProperty(value = "币种id",required = true)
    private Long currencyId;
}
