package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel
public class FilBillDetailVO {

    @ApiModelProperty("账单id")
    private Long billId;

    @ApiModelProperty("发送方")
    private String sender;

    @ApiModelProperty("接收方")
    private String receiver;

    @ApiModelProperty("金额")
    private BigDecimal money;

    @ApiModelProperty("0-销毁手续费;1-节点手续费;2-转账")
    private Integer type;

    @ApiModelProperty("创建时间")
    private Date createTime;

}
