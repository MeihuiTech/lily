package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvertBzzVO {

    @ApiModelProperty(value = "可兑换BZZ",required = true)
    private BigDecimal convertBzz;

    @ApiModelProperty(value = "时间",required = true)
    private LocalDateTime dateTime;
}
