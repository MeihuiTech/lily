package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 聚合统计表
 */
@Data
@TableName("swarm_one_hour_agg")
public class SwarmAgg {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 节点唯一标识
     */
    private String nodeId;

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
    private LocalDateTime date;

    private LocalDateTime createTime;
}