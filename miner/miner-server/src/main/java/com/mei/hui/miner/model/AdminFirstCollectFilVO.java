package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理员首页-旷工统计数据出参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/5/28 17:26
 **/
@Data
@ApiModel("管理员首页-旷工统计数据出参")
public class AdminFirstCollectFilVO {

    @ApiModelProperty(value = "平台总资产")
    private BigDecimal allBalanceMinerAccount;

    @ApiModelProperty(value = "平台有效算力")
    private BigDecimal allPowerAvailable;

    @ApiModelProperty(value = "活跃旷工")
    private Long allMinerCount;

    @ApiModelProperty(value = "当天出块份数")
    private Long allBlocksPerDay;

}
