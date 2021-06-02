package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
@ApiModel(value = "chia币矿工详情出参")
public class XchMinerDetailBO {

    @ApiModelProperty("有效算力, 单位B")
    private BigDecimal powerAvailable;

    @ApiModelProperty("累计出块奖励,单位XCH")
    private BigDecimal totalBlockAward;

    @ApiModelProperty("总资产, 单位XCH")
    private BigDecimal balanceMinerAccount;

    @ApiModelProperty("累计出块份数")
    private Long totalBlocks;

    @ApiModelProperty("当天出块份数")
    private Long blocksPerDay;

    @ApiModelProperty("算力增速, 单位B")
    private BigDecimal powerIncreasePerDay;

}
