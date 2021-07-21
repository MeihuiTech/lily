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
public class FilBillPageListVO {

    @ApiModelProperty("旷工id")
    private String minerId;

    @ApiModelProperty("消息id")
    private String messageId;

    @ApiModelProperty("区块高度")
    private Long blockHeight;

    @ApiModelProperty("发送方")
    private String sender;

    @ApiModelProperty("接收方")
    private String receiver;

    @ApiModelProperty("方法")
    private String method;

    @ApiModelProperty("金额")
    private BigDecimal money;

    @ApiModelProperty("0-转出;1-转入")
    private Integer type;

    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty("0-Owner；1-Worker；2-Controller")
    private String accountType;

    @ApiModelProperty("账单产生时间")
    private Date dateTime;

}
