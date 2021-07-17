package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/17 10:44
 **/
@Data
@ApiModel(value = "矿工节点经纬度出参")
public class MinerLongitudeLatitudeVO {

    @ApiModelProperty(value = "矿工id")
    private String minerId;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "纬度")
    private BigDecimal latitude;

    @ApiModelProperty(value = "经度")
    private BigDecimal longitude;

}
