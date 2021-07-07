package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "管理员-用户列表出参")
public class DiskBO {

    @ApiModelProperty(value = "剩余物理容量",required = true)
    private String minerId;
}
