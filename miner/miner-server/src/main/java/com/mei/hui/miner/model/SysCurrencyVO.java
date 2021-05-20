package com.mei.hui.miner.model;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 不分页排序查询币种列表出参
 * @author shangbin
 * @version v1.0.0
 * @date 2021/5/14 14:54
 **/
@Data
@Api("不分页排序查询币种列表出参")
public class SysCurrencyVO {

    @ApiModelProperty(value = "币种id")
    private Integer id;

    @ApiModelProperty(value = "币种名称")
    private String name;

    @ApiModelProperty(value = "代币的简称：fil")
    private String type;

}
