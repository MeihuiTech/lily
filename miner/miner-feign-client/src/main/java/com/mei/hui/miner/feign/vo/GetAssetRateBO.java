package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class GetAssetRateBO {

    @ApiModelProperty(value = "用户id",required = true)
    private Long userId;
}
