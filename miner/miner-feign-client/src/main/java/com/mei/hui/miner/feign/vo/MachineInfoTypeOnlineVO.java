package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 按照机器类型、是否在线分组查询矿机信息表的数量
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/6 19:27
 **/
@Data
@ApiModel("按照机器类型、是否在线分组查询矿机信息表的数量")
public class MachineInfoTypeOnlineVO {


    @ApiModelProperty(value = "机器类型")
    private String machineType;

    @ApiModelProperty(value = "在线状态：0 离线 1在线")
    private Integer online;

    @ApiModelProperty(value = "机器数量")
    private Integer count;

}
