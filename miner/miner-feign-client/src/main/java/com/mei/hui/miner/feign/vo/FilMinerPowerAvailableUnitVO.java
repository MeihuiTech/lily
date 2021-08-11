package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 矿工有效算力单位换算
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/10 16:31
 **/
@Data
public class FilMinerPowerAvailableUnitVO {


    @ApiModelProperty(value = "有效算力, 单位B")
    private BigDecimal powerAvailable;

    @ApiModelProperty(value = "有效算力单位")
    private String powerAvailableUnit;


}
