package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("swarm_agg")
public class SwarmAgg {
    private Long id;

    private String nodeIp;

    private Integer nodePort;

    private Long linkNum;

    private Long ticketAvail;

    private Long ticketValid;

    private BigDecimal convertBzz;

    private Date date;

    private Date createTime;

}