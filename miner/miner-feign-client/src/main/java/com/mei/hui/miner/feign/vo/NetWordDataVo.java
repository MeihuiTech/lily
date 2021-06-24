package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel("全网数据")
public class NetWordDataVo {

    @ApiModelProperty("全网累计出块奖励")
    private BigDecimal totalBlockAward;

    @ApiModelProperty("全网算力")
    private BigDecimal power;

    @ApiModelProperty("全网今日出块数")
    private Long perDayBlocks;

    @ApiModelProperty("全网活跃旷工")
    private Long activeMiner;

}
