package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@ApiModel
@Accessors(chain = true)
public class FindDiskSizeInfoBO {

    @ApiModelProperty("磁盘大小")
    private BigDecimal size;

    @ApiModelProperty("名称")
    private String clusterName;
}
