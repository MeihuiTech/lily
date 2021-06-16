package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理员首页-平台有效出票数排行榜出参
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 16:26
 **/
@Data
@ApiModel("管理员首页-平台有效出票数排行榜出参")
public class SwarmTicketValidVO {


    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "总有效出票数")
    private Long ticketValid;

    @ApiModelProperty(value = "总有效出票数所占百分比")
    private BigDecimal ticketValidPercent;

    @ApiModelProperty(value = "今日有效出票数")
    private Long todayTicketValid;

    @ApiModelProperty(value = "有效节点")
    private Long nodeValid;

    @ApiModelProperty(value = "平台总连接数")
    private Long linkNum;


}
