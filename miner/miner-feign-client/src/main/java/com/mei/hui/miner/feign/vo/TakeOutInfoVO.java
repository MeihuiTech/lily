package com.mei.hui.miner.feign.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel
public class TakeOutInfoVO {

    @ApiModelProperty("平台佣金")
    private BigDecimal fee;

    @ApiModelProperty("提现到账金额")
    private BigDecimal arriveMoney;

    @ApiModelProperty("截止到当前时间,锁仓奖励")
    private BigDecimal lockAward;

    @ApiModelProperty("截止到当前时间,累计出块奖励")
    private BigDecimal totalBlockAward;
}
