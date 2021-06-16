package com.mei.hui.miner.feign.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@ApiModel
@Data
public class NodePageListVO {

    @ApiModelProperty(value = "钱包地址，节点唯一标识")
    private String walletAddress;

    @ApiModelProperty(value = "节点ip")
    private String nodeIp;

    @ApiModelProperty(value = "节点port")
    private Integer nodePort;

    @ApiModelProperty(value = "出票资产")
    private BigDecimal money;

    @ApiModelProperty(value = "连接数")
    private long linkNum;

    @ApiModelProperty(value = "合约地址")
    private String contractAddress;

    @ApiModelProperty(value = "已兑换")
    private BigDecimal changed;

    @ApiModelProperty(value = "未兑换")
    private BigDecimal noChange;

    @ApiModelProperty(value = "状态:0-停用;1-挖矿中")
    private Short state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "累计有效出票数")
    private long ticketValid;

    @ApiModelProperty(value = "昨日有效出票数")
    private long yestodayTicketValid;


}
