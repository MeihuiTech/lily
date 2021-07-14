package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 七牛云集群硬盘容量和宽带出参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/13 17:04
 **/
@Data
@ApiModel(value = "七牛云集群硬盘容量和宽带出参")
public class QiniuVO {

    @ApiModelProperty(value = "集群容量出参")
    private DiskSizeVO diskSizeVO;

    @ApiModelProperty(value = "宽带信息出参")
    private BroadbandVO broadbandVO;

}
