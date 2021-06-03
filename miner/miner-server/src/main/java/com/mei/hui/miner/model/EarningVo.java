package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("fil查询用户收益出参")
public class EarningVo {

    @ApiModelProperty(value = "总收益")
    private double totalEarning;

    @ApiModelProperty(value = "总锁仓收益")
    private double totalLockAward;

    @ApiModelProperty(value = "用户总共已提取")
    private double totalWithdraw;

    @ApiModelProperty(value = "用户可提取金额")
    private double availableEarning;

    @ApiModelProperty(value = "正在提币中的")
    private double drawingEarning;

    public EarningVo() {
    }

    public EarningVo(double totalEarning, double totalLockAward, double totalWithdraw, double availableEarning) {
        this.totalEarning = totalEarning;
        this.totalLockAward = totalLockAward;
        this.totalWithdraw = totalWithdraw;
        this.availableEarning = availableEarning;
    }


}
