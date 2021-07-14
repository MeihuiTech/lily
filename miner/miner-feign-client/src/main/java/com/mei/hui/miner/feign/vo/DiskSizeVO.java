package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * 七牛云集群硬盘容量
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/13 17:17
 **/
@Data
@ApiModel(value = "七牛云集群硬盘容量")
public class DiskSizeVO {


    @ApiModelProperty(value = "集群总物理容量")
    private BigDecimal allDiskSize;

    @ApiModelProperty(value = "剩余物理容量")
    private BigDecimal availDiskSize;

    @ApiModelProperty(value = "已用物理容量")
    private BigDecimal usedDiskSize;

    @ApiModelProperty(value = "矿工已用物理容量")
    private List<MinerDiskSizeVO> minerUsedDiskSizeVOList;

}
