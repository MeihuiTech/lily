package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@ApiModel
@Accessors(chain = true)
public class AccountInfoVO {

    @ApiModelProperty("矿工id")
    private String minerId;

    @ApiModelProperty("可用余额")
    private BigDecimal balanceMinerAvailable;

    @ApiModelProperty("worker账户余额")
    private BigDecimal balanceWorkerAccount;

    @ApiModelProperty("post账户余额")
    private BigDecimal balancePostAccount;

}
