package com.mei.hui.miner.feign.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel
public class TicketPageListVO {

    @ApiModelProperty("节点ip")
    private String nodeIp;

    @ApiModelProperty("节点port")
    private Integer nodePort;

    @ApiModelProperty("类型:0-无效;1-有效")
    private Integer type;

    @ApiModelProperty("类型:0-无效;1-有效")
    private Integer state;

    @ApiModelProperty("面值")
    private BigDecimal parValue;

    @ApiModelProperty("per")
    private String peer;

    @ApiModelProperty("beneficiary")
    private String beneficiary;

    @ApiModelProperty("chequebook")
    private String chequebook;

    @ApiModelProperty("hash")
    private String hash;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;
}
