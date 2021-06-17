package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理员-用户收益-用户列表出参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/17 13:50
 **/
@Data
@ApiModel(value = "管理员-用户收益-用户列表出参")
public class SwarmUserMoneyVO {

    @ApiModelProperty(value = "用户Id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "累计有效出票数")
    private Long ticketValid;

    @ApiModelProperty(value = "出票资产")
    private BigDecimal money;


}
