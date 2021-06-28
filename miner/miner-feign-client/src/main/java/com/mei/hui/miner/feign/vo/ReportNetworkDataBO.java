package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
@ApiModel
public class ReportNetworkDataBO {

    @ApiModelProperty(value = "全网累计出块奖励",required = true)
    private BigDecimal totalBlockAward;

    @ApiModelProperty(value = "全网有效算力",required = true)
    private BigDecimal power;

    @ApiModelProperty(value = "全网累计出块份数",required = true)
    private Long blocks;

    @ApiModelProperty(value = "全网区块高度",required = true)
    private Long blockHeight;

    @ApiModelProperty(value = "全网活跃旷工",required = true)
    private Long activeMiner;
}
