package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 矿工物理容量
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/13 17:49
 **/
@Data
@ApiModel(value = "矿工已用物理容量")
@Accessors(chain = true)
public class MinerDiskSizeVO {

    @ApiModelProperty(value = "矿工id")
    private String minerId;

    @ApiModelProperty("矿工物理容量")
    private BigDecimal diskSize;

}
