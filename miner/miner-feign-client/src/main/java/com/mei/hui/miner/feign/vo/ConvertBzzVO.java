package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel
@Data
public class ConvertBzzVO {

    @ApiModelProperty(value = "可兑换BZZ",required = true)
    private BigDecimal convertBzz;

    @ApiModelProperty(value = "时间",required = true)
    private LocalDateTime dateTime;
}
