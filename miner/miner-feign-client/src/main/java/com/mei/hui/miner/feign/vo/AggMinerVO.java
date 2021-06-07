package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "管理员-用户列表出参")
public class AggMinerVO {

    @ApiModelProperty(value = "总算力")
    private BigDecimal powerAvailable;

    @ApiModelProperty(value = "总收益")
    private BigDecimal totalBlockAward;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "费率")
    private BigDecimal feeRate;

}
