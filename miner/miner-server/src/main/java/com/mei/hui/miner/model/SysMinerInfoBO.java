package com.mei.hui.miner.model;

import com.mei.hui.miner.entity.SysMinerInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 矿工列表入参
 * @author shangbin
 * @version v1.0.0
 * @date 2021/5/24 15:05
 **/
@Data
@ApiModel("矿工列表入参")
public class SysMinerInfoBO extends SysMinerInfo {


    @ApiModelProperty(value = "true 升序，false 降序")
    private boolean isAsc;

    @ApiModelProperty(value = "排序字段名称")
    private String cloumName;


}
