package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("swarm_ticket")
public class SwarmTicket {
    private Long id;

    private String nodeIp;

    private Integer nodePort;

    private Byte type;

    private Byte state;

    private BigDecimal parValue;

    private String peer;

    private String beneficiary;

    private String chequebook;

    private String hash;

    private Date createTime;

}