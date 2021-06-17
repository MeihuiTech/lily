package com.mei.hui.miner.feign.vo;

import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class TicketPageListBO extends BasePage {

    @ApiModelProperty(value = "ip地址")
    private String nodeIp;

    @ApiModelProperty(value = "类型:0-无效;1-有效")
    private Integer type;
}
