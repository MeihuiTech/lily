package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class GetUserEarningInput {

    @ApiModelProperty(value = "矿工id",required = true)
    private String minerId;
}
