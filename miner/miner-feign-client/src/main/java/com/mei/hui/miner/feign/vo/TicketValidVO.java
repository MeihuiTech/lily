package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@ApiModel
@Data
public class TicketValidVO {

    @ApiModelProperty(value = "有效票数",required = true)
    private long ticketValidNum;

    @ApiModelProperty(value = "时间",required = true)
    private LocalDateTime dateTime;
}
