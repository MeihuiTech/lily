package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * FIL币账单消息每天汇总表出参
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/17 17:20
 **/
@Data
@ApiModel("FIL币账单消息每天汇总表出参")
public class FilBillDayAggVO {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "收入")
    private BigDecimal inMoney;

    @ApiModelProperty(value = "支出")
    private BigDecimal outMoney;

    @ApiModelProperty(value = "结余")
    private BigDecimal balance;


}
