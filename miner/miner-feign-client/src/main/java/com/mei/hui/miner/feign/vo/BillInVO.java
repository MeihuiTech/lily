package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel
public class BillInVO {

    @ApiModelProperty("区块奖励")
    private BigDecimal blockAward;

    @ApiModelProperty("其他转入")
    private BigDecimal other;

    @ApiModelProperty("收入总额")
    private BigDecimal total;
}
