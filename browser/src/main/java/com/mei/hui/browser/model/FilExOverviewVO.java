package com.mei.hui.browser.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 首页全网概览
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/19 14:05
 **/
@Data
@ApiModel("首页全网概览")
public class FilExOverviewVO {


    @ApiModelProperty(value = "区块高度")
    private Long height;

    @ApiModelProperty(value = "全网有效算力(bytes)")
    private BigDecimal totalQaBytesPower;

    @ApiModelProperty(value = "近24小时增长算力(bytes)")
    private BigDecimal totalQaBytesPowerIncrease24h;

    @ApiModelProperty(value = "32GB扇区gas消耗(Fil/TiB)")
    private BigDecimal thirtyTwoGas;

    @ApiModelProperty(value = "64GB扇区gas消耗(Fil/TiB)")
    private BigDecimal sixtyFourGas;

    @ApiModelProperty(value = "当前扇区质押量(Fil/TiB)")
    private BigDecimal sectorPledge;

    @ApiModelProperty(value = "近24小时产出效率(Fil/TiB)")
    private BigDecimal outputEfficiency24h;

    @ApiModelProperty(value = "当前基础费率(autoFil)")
    private BigDecimal baseFee;

    /**
     * 64GiB扇区新增算力成本=64GiB扇区Gas消耗+当前扇区质押量
     */
    @ApiModelProperty(value = "64GiB扇区新增算力成本")
    private BigDecimal sixtyFourNewPowerCost;

    /**
     * 32GiB扇区新增算力成本=32GiB扇区Gas消耗+当前扇区质押量
     */
    @ApiModelProperty(value = "32GiB扇区新增算力成本")
    private BigDecimal thirtyTwoNewPowerCost;

    @ApiModelProperty(value = "每区块奖励(autoFil)")
    private BigDecimal blockReward;

    @ApiModelProperty(value = "全网出块奖励(autoFil)")
    private BigDecimal minedFil;

    @ApiModelProperty(value = "销毁量(autoFil)")
    private BigDecimal burntFil;

    @ApiModelProperty(value = "活跃节点")
    private Long participatingMinerCount;

    @ApiModelProperty(value = "全网出块数量")
    private Long blockCount;


}
