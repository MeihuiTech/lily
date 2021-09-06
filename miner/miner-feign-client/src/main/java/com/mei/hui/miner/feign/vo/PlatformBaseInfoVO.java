package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@ApiModel
@Data
@Accessors(chain = true)
public class PlatformBaseInfoVO {

    @ApiModelProperty(value = "平台总资产")
    private BigDecimal totalAccount;

    @ApiModelProperty(value = "平台有效算力,单位B")
    private BigDecimal allPowerAvailable;

    @ApiModelProperty(value = "活跃矿工")
    private int allMinerCount;

    @ApiModelProperty(value = "平台累计出块")
    private long totalBlocks;

    @ApiModelProperty(value = "24小时出块份数")
    private long twentyFourBlocks;

    @ApiModelProperty(value = "在线设备数")
    private int machineOnlineNum;

}
