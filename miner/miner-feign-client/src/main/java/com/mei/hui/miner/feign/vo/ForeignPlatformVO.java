package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@ApiModel("对外API-平台数据")
public class ForeignPlatformVO {

    @ApiModelProperty("平台累计出块奖励")
    private BigDecimal totalBlockAward;

    @ApiModelProperty("平台算力")
    private BigDecimal power;

    @ApiModelProperty("平台今日出块数")
    private Long perDayBlocks;

    @ApiModelProperty("平台活跃旷工")
    private Long activeMiner;

}
