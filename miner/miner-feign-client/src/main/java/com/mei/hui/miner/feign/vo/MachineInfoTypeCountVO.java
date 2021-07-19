package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/19 16:23
 **/
@Data
@ApiModel(value = "各种矿机类型的数量")
public class MachineInfoTypeCountVO {

    @ApiModelProperty(value = "Miner在线矿机数量")
    private Integer minerOnlineMachineCount;

    @ApiModelProperty(value = "Miner离线矿机数量")
    private Integer minerOfflineMachineCount;

    @ApiModelProperty(value = "post在线矿机数量")
    private Integer postOnlineMachineCount;

    @ApiModelProperty(value = "post离线矿机数量")
    private Integer postOfflineMachineCount;

    @ApiModelProperty(value = "c2在线矿机数量")
    private Integer ctwoOnlineMachineCount;

    @ApiModelProperty(value = "c2离线矿机数量")
    private Integer ctwoOfflineMachineCount;

    @ApiModelProperty(value = "seal在线矿机数量")
    private Integer sealOnlineMachineCount;

    @ApiModelProperty(value = "seal离线矿机数量")
    private Integer sealOfflineMachineCount;

}
