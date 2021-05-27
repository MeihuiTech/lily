package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
@ApiModel
public class XchMinerDetailBO {

    @ApiModelProperty("有效算力, 单位B")
    private BigDecimal powerAvailable;

    @ApiModelProperty("累计出块奖励,单位XCH")
    private BigDecimal totalBlockAward;

    @ApiModelProperty("总资产, 单位XCH")
    private BigDecimal balanceMinerAccount;

    @ApiModelProperty("出块分数")
    private Long blocksAmount;

    @ApiModelProperty("算力增长,单位B")
    private BigDecimal powerIncrease;

}
