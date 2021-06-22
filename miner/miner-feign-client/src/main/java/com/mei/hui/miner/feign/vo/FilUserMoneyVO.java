package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/22 16:25
 **/
@Data
@ApiModel(value = "管理员-用户收益-用户收益列表")
public class FilUserMoneyVO {


    @ApiModelProperty(value = "用户Id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "总算力")
    private BigDecimal powerAvailable;

    @ApiModelProperty(value = "总收益")
    private BigDecimal totalBlockAward;

    @ApiModelProperty(value = "费率")
    private BigDecimal feeRate;


}
