package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidVO {

    @ApiModelProperty(value = "有效票数",required = true)
    private long ticketValidNum;

    @ApiModelProperty(value = "时间",required = true)
    private LocalDateTime dateTime;
}
