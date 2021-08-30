package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "账单列表入参")
public class ExportBillBO {


    @ApiModelProperty(value = "矿工id",required = true)
    private String minerId;

    @ApiModelProperty(value = "日期：月汇总、日账单接口格式为：2021-08",required = true)
    private String monthDate;
}
