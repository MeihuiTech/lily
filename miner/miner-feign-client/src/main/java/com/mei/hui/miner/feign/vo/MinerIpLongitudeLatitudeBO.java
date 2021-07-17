package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 上报矿工ip入参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/16 17:54
 **/
@Data
@ApiModel(value = "上报矿工ip入参")
public class MinerIpLongitudeLatitudeBO {

    @ApiModelProperty(value = "矿工id")
    private String minerId;

    @ApiModelProperty(value = "ip")
    private String ip;

}
