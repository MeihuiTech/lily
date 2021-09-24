package com.mei.hui.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * mysql、redis、mq、矿工模块宕机运维报警功能
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/9/22 14:31
 **/
@ApiModel(value = "mysql、redis、mq、矿工模块宕机运维报警功能")
@Data
public class CheckConnectionStatusVO {

//    @ApiModelProperty(value = "mysql状态：on在线，off离线")
//    private String mysql;

    @ApiModelProperty(value = "redis状态：on在线，off离线")
    private String redis;

    @ApiModelProperty(value = "mq状态：on在线，off离线")
    private String mq;

    @ApiModelProperty(value = "矿工模块：on在线，off离线")
    private String  miner;

    @ApiModelProperty(value = "时间")
    private String dateTime;


}
