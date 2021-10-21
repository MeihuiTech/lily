package com.mei.hui.browser.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 近30天有效算力走势
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/19 17:47
 **/
@Data
@ApiModel("近30天有效算力走势")
public class FilExBaseLineTrendVO {


    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "基线算力(byte)")
    private BigDecimal baselinePower;

    @ApiModelProperty(value = "全网有效算力(byte)")
    private BigDecimal totalQaBytesPower;


}
