package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/6 16:12
 **/
@Data
@ApiModel(value = "方法对应的金额实体")
public class BillMethodMoneyVO {


    @ApiModelProperty(value = "方法")
    private String method;

    @ApiModelProperty(value = "金额")
    private BigDecimal money;


}
