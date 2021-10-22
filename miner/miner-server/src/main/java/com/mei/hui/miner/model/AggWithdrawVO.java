package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("用户提币汇总分页出参")
public class AggWithdrawVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("用户主键")
    private Long sysUserId;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("提现总额")
    private BigDecimal takeTotalMony;

    @ApiModelProperty("提现缴纳平台费用总额")
    private BigDecimal totalFee;

    @ApiModelProperty("提现次数")
    private Integer tatalCount;

    @ApiModelProperty("货币种类,FIL,CHIA")
    private String type;

    @ApiModelProperty(value = "提取类型：0提取收益，1提取转质押")
    private Integer pledgeType;

}
