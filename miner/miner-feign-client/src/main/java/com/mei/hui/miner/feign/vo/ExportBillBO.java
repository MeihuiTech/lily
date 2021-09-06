package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel(value = "账单列表入参")
public class ExportBillBO {


    @ApiModelProperty(value = "矿工id",required = true)
    private String minerId;

    @ApiModelProperty(value = "开始日期，格式为：2021-08-01",required = true)
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期，格式为：2021-08-01",required = true)
    private LocalDate endDate;
}
