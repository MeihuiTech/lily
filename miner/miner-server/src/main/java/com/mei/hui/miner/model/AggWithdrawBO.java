package com.mei.hui.miner.model;

import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel("管理员-矿池收益-用户提币汇总分页入参")
public class AggWithdrawBO extends BasePage {

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty(value = "币种id")
    private Long currencyId;

}
