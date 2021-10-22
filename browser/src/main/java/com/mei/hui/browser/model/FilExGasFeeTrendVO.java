package com.mei.hui.browser.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扇区封装Gas费用
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/19 17:19
 **/
@Data
@ApiModel("扇区封装Gas费用")
public class FilExGasFeeTrendVO {

    @ApiModelProperty(value = "时间")
    private String time;

    @ApiModelProperty(value = "32GB存储封Gas消耗(Fil/TiB)")
    private BigDecimal thirtyTwoGas;

    @ApiModelProperty(value = "64GB存储封Gas消耗(Fil/TiB)")
    private BigDecimal sixtyFourGas;


}
