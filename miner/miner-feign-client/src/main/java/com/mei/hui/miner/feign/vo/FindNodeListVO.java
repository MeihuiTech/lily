package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class FindNodeListVO {

    @ApiModelProperty("钱包地址")
    private String walletAddress;

    @ApiModelProperty("节点ip")
    private String nodeIp;

    @ApiModelProperty("节点端口")
    private Integer nodePort;

    @ApiModelProperty("状态:0-停用;1-挖矿中")
    private Short state;

}
