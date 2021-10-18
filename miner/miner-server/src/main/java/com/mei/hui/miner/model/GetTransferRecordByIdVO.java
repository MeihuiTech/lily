package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@ApiModel
@Accessors(chain = true)
public class GetTransferRecordByIdVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("可结算奖励【普通用户提币时所填】")
    private BigDecimal amount;

    @ApiModelProperty("服务费【普通用户提币时所填】")
    private BigDecimal fee;

    @ApiModelProperty("可结算奖励【当前最新值】")
    private BigDecimal newAmount;

    @ApiModelProperty("服务费【当前最新值】")
    private BigDecimal newFee;

    @ApiModelProperty("费用HASH")
    private String feeHash;

    @ApiModelProperty("提取地址")
    private String toAddress;

    @ApiModelProperty("提取HASH")
    private String toHash;

    @ApiModelProperty("矿工")
    private String minerId;

    @ApiModelProperty("0-提币中;1-提币完成;2-提币失败")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("截止到当前时间,累计出块奖励")
    private BigDecimal totalBlockAward;

    @ApiModelProperty("截止到当前时间,锁仓奖励")
    private BigDecimal lockAward;

    @ApiModelProperty("本次解锁奖励")
    private BigDecimal unLockAward;

    @ApiModelProperty("上次解锁奖励")
    private BigDecimal prevUnlockAward;

    @ApiModelProperty("实结收益")
    private BigDecimal realMoney;

}
