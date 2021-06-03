package com.mei.hui.miner.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel("chia查询矿工信息列表出参")
@Data
public class ChiaMinerVO {

    private Long id;

    @ApiModelProperty(value = "旷工id")
    private String minerId;

    @ApiModelProperty(value = "有效算力, 单位B")
    private BigDecimal powerAvailable;

    @ApiModelProperty(value = "累计出块奖励,单位XCH")
    private BigDecimal totalBlockAward;

    @ApiModelProperty(value = "总资产, 单位XCH")
    private BigDecimal balanceMinerAccount;

    @ApiModelProperty(value = "累计出块份数")
    private Long totalBlocks;

}
