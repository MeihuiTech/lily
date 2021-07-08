package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel
public class DiskVO {

    @ApiModelProperty("剩余物理容量")
    private BigDecimal availDiskSize;

    @ApiModelProperty("已用物理容量")
    private BigDecimal usedDiskSize;

    @ApiModelProperty("剩余可写逻辑容量")
    private BigDecimal logicalAvailSize;

    @ApiModelProperty("矿工已用存储量")
    private BigDecimal minerUsedDiskSize;

    @ApiModelProperty("剩余容量可用天数预测")
    private Integer days;

    @ApiModelProperty("过去5天平均使用容量")
    private BigDecimal usedSizeAvg;



}
