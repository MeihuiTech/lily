package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 聚合统计表
 */
@Data
@TableName("swarm_agg")
public class SwarmAgg {
    private Long id;

    /**
     * 钱包地址，节点唯一标识
     */
    private String walletAddress;

    /**
     * 连接数量
     */
    private Long linkNum;

    /**
     * 无效票数
     */
    private Long ticketAvail;

    /**
     * 有效票数
     */
    private Long ticketValid;

    /**
     * 可兑换BZZ
     */
    private BigDecimal convertBzz;

    /**
     * 日期
     */
    private Date date;

    private Date createTime;

}