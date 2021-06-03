package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理员首页-平台有效算力排行榜出参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/5/28 18:57
 **/
@Data
@ApiModel("管理员首页-平台有效算力排行榜出参")
public class PowerAvailableFilVO {

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "有效算力, 单位B")
    private BigDecimal powerAvailable;

    @ApiModelProperty(value = "有效算力所占百分比")
    private BigDecimal powerAvailablePercent;

    @ApiModelProperty(value = "累计出块奖励,单位FIL")
    private BigDecimal totalBlockAward;

    @ApiModelProperty(value = "挖矿效率")
    private BigDecimal miningEfficiency;

    @ApiModelProperty(value = "算力增速")
    private BigDecimal powerIncrease;

}
