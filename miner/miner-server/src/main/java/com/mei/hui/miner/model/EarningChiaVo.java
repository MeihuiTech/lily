package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("chia查询用户收益出参")
public class EarningChiaVo {


    @ApiModelProperty(value = "用户总共已提取")
    private double totalWithdraw;

    @ApiModelProperty(value = "用户可提取金额")
    private double availableEarning;

    @ApiModelProperty(value = "正在提币中的chia")
    private double drawingEarning;

    public EarningChiaVo() {
    }

    public EarningChiaVo(double totalWithdraw, double availableEarning) {
        this.totalWithdraw = totalWithdraw;
        this.availableEarning = availableEarning;
    }


}
