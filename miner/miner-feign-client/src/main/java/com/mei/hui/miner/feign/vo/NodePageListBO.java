package com.mei.hui.miner.feign.vo;

import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class NodePageListBO extends BasePage {

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "状态:0-停用;1-挖矿中")
    private Integer state;
}
