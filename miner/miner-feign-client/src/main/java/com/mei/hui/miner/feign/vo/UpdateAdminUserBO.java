package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel
public class UpdateAdminUserBO {

    @ApiModelProperty(value = "管理员用户id",required = true)
    private Long adminId;

    @ApiModelProperty(value = "普通矿工用户id",required = true)
    private Long userId;
}
