package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 节点出票信息
 */
@Data
@TableName("swarm_ticket")
public class SwarmTicket {
    private Long id;

    private String nodeIp;

    private Integer nodePort;

    private Integer type;

    private BigDecimal parValue;

    private String peer;

    private String beneficiary;

    private String chequebook;

    private String hash;

    private Date createTime;

}