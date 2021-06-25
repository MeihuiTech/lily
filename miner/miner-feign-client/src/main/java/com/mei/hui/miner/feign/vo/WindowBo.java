package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class WindowBo {

    @ApiModelProperty(value = "窗口序号",required = true)
    private Integer deadline;

    @ApiModelProperty(value = "partitions",required = true)
    private Integer partitions;

    @ApiModelProperty(value = "扇区数量",required = true)
    private Integer sectors;

    @ApiModelProperty(value = "错误扇区数量",required = true)
    private Integer sectorsFaults;

    @ApiModelProperty(value = "proven_partitions",required = true)
    private Long provenPartitions;

    @ApiModelProperty(value = "是否是当前窗口,1-是当前窗口，0-不是",required = true)
    private Integer isCurrent;
}
