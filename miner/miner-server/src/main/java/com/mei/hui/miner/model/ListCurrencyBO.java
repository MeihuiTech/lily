package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel
public class ListCurrencyBO {

    @ApiModelProperty("币种列表")
    private List<SysCurrencyVO> list = new ArrayList<>();

    @ApiModelProperty("当前使用的币种id")
    private Long currencyId;

}
