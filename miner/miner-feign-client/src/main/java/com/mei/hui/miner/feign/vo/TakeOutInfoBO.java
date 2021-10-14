package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel
public class TakeOutInfoBO {

    @ApiModelProperty(value = "矿工id",required = true)
    private String minerId;
}
