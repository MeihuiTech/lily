package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * swarm节点信息
 */
@Data
@TableName("swarm_node")
public class SwarmNode {
    private Long id;

    /**
     * 钱包地址，节点唯一标识
     */
    private String walletAddress;

    private Long userid;

    /**
     * 节点ip
     */
    private String nodeIp;

    /**
     * 节点端口
     */
    private Integer nodePort;

    /**
     * 出票资产
     */
    private BigDecimal money;

    /**
     * 有效出票数
     */
    private Long ticketValid;

    /**
     * 无效出票数
     */
    private Long ticketAvail;

    /**
     * 连接数
     */
    private Long linkNum;

    /**
     * 合约地址
     */
    private String contractAddress;

    /**
     * 已兑换
     */
    private BigDecimal changed;

    /**
     * 未兑换
     */
    private BigDecimal noChange;

    /**
     * 状态:0-停用;1-挖矿中
     */
    private Short state;

    private Date createTime;

}