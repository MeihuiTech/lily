package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 在线/离线矿机数量
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/5 19:58
 **/
@Data
@ApiModel("在线/离线矿机数量")
public class OnlineMachineCountVO {

    @ApiModelProperty(value = "矿机状态：0 离线 1在线")
    private Integer online;


    @ApiModelProperty(value = "矿机数量")
    private Long count;


}
