package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("swarm_node")
public class SwarmNode {
    private Long id;

    private Long userid;

    private String nodeIp;

    private Integer nodePort;

    private BigDecimal money;

    private Long ticketValid;

    private Long ticketAvail;

    private Long linkNum;

    private String walletAddress;

    private String contractAddress;

    private BigDecimal changed;

    private BigDecimal noChange;

    private Short state;

    private Date createTime;

}