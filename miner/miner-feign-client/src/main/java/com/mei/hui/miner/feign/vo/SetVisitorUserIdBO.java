package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@ApiModel
public class SetVisitorUserIdBO {

    @ApiModelProperty(value = "用户id",required = true)
    private Long userId;
}
