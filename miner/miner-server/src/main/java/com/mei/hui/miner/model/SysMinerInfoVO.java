package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/5/25 15:40
 **/
@Data
@ApiModel("矿工列表出参")
public class SysMinerInfoVO {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "矿工ID")
    private String minerId;

    @ApiModelProperty(value = "挖矿账户余额, 单位FIL")
    private BigDecimal balanceMinerAccount;

    @ApiModelProperty(value = "矿工可用余额,单位FIL")
    private BigDecimal balanceMinerAvailable;

    @ApiModelProperty(value = "扇区质押, 单位FIL")
    private BigDecimal sectorPledge;

    @ApiModelProperty(value = "累计出块奖励,单位FIL")
    private BigDecimal totalBlockAward;

    @ApiModelProperty(value = "有效算力, 单位B")
    private BigDecimal powerAvailable;

    @ApiModelProperty(value = "在线矿机数量")
    private Long onlineMachineCount;

    @ApiModelProperty(value = "离线矿机数量")
    private Long offMachineCount;

    @ApiModelProperty(value = "算力增速, 单位B")
    private BigDecimal powerIncreasePerDay;

    @ApiModelProperty(value = "累计出块份数")
    private Long totalBlocks;

    @ApiModelProperty(value = "当天出块份数")
    private Long blocksPerDay;

    @ApiModelProperty(value = "worker账户余额")
    private BigDecimal balanceWorkerAccount;

    @ApiModelProperty(value = "有效状态扇区数量")
    private Integer sectorAvailable;

    @ApiModelProperty(value = "错误状态扇区数量")
    private Integer sectorError;

    @ApiModelProperty(value = "PoSt账户余额")
    private BigDecimal postBalance;

}
