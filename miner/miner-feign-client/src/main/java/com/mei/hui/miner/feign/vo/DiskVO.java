package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "管理员-用户列表出参")
public class DiskVO {

    @ApiModelProperty(value = "剩余物理容量")
    private BigDecimal availDiskSize;

    @ApiModelProperty(value = "已用物理容量")
    private BigDecimal usedDiskSize;

    @ApiModelProperty(value = "剩余可写逻辑容量")
    private BigDecimal logicalAvailSize;

    @ApiModelProperty(value = "旷工已用存储量")
    private BigDecimal minerUsedDiskSize;

}
