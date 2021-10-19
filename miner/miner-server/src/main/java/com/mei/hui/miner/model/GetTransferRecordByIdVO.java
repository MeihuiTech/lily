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

    @ApiModelProperty("服务费率")
    private BigDecimal feeRate = BigDecimal.ZERO;

    //newAmount = realMoney + newFee
    @ApiModelProperty("理论收益【只在查看时显示】")
    private BigDecimal newAmount;

    @ApiModelProperty("服务费【只在查看时显示】")
    private BigDecimal newFee;

    @ApiModelProperty("实际收益【只在查看时显示】")
    private BigDecimal realMoney;


}
