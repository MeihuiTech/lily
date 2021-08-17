package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/5 17:45
 **/
@Data
@ApiModel(value = "账单消息列表出参")
public class FilBillVO {


    @ApiModelProperty(value = "账单产生时间")
    private LocalDateTime dateTime;

    @ApiModelProperty(value = "消息id")
    private String cid;

    @ApiModelProperty(value = "收支：0支出，1收入")
    private Integer inOrOut;

    @ApiModelProperty(value = "金额")
    private BigDecimal money;

    @ApiModelProperty(value = "方法")
    private String method;

    @ApiModelProperty(value = "类型：0Node Fee矿工手续费，1Burn Fee燃烧手续费，2Transfer转账，3BlockAward区块奖励")
    private String type;

    @ApiModelProperty(value = "发送方")
    private String sender;

    @ApiModelProperty(value = "接收方")
    private String receiver;


}
