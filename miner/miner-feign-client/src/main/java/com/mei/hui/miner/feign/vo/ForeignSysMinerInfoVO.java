package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
* 对外API-矿工数据出参
*
* @description
* @author shangbin
* @date 2021/7/5 11:19
* @param
* @return
* @version v1.4.1
*/
@Data
@ApiModel("对外API-矿工数据出参")
public class ForeignSysMinerInfoVO
{

    @ApiModelProperty(value = "有效算力, 单位B")
    private BigDecimal powerAvailable;

    @ApiModelProperty(value = "扇区质押, 单位FIL")
    private BigDecimal sectorPledge;

    @ApiModelProperty(value = "扇区大小,单位GB")
    private Long sectorSize;

    @ApiModelProperty(value = "有效扇区、证明状态扇区数量")
    private Integer sectorProving;

    @ApiModelProperty(value = "扇区总数、有效状态扇区数量")
    private Integer sectorAvailable;

    @ApiModelProperty(value = "错误状态扇区数量")
    private Integer sectorError;

    @ApiModelProperty(value = "挖矿琐仓、锁仓收益, 单位FIL")
    private BigDecimal lockAward;

    @ApiModelProperty(value = "累计出块奖励,单位FIL")
    private BigDecimal totalBlockAward;

    @ApiModelProperty(value = "矿工可用余额,单位FIL")
    private BigDecimal balanceMinerAvailable;

    @ApiModelProperty(value = "总资产、挖矿账户余额, 单位FIL")
    private BigDecimal balanceMinerAccount;

    @ApiModelProperty(value = "Worker账户余额")
    private BigDecimal balanceWorkerAccount;

    @ApiModelProperty(value = "当天出块份数")
    private Long blocksPerDay;

    @ApiModelProperty(value = "累计出块份数")
    private Long totalBlocks;

    @ApiModelProperty(value = "算力增速, 单位B")
    private BigDecimal powerIncreasePerDay;

    @ApiModelProperty(value = "PoSt账户余额")
    private BigDecimal postBalance;

}
