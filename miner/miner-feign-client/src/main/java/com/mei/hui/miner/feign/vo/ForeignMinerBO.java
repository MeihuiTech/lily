package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 对外API-矿工数据入参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/5 11:20
 **/
@Data
@ApiModel("对外API-矿工数据")
public class ForeignMinerBO {

    @ApiModelProperty(value = "矿工id")
    private String minerId;

}
