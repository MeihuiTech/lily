package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * fil币区块奖励详情表
 */
@Data
@TableName("fil_block_award")
public class FilBlockAward {


    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 矿工id，上报接口提供，可能上报账单的时候矿工id在矿工表有可能不存在
     */
    private String minerId;

    /**
     * 区块id
     */
    private String cid;

    /**
     * 区块奖励
     */
    private BigDecimal blockReward;

    /**
     * 矿工费用
     */
    private BigDecimal minerFee;

    /**
     * 权重
     */
    private Long parentweight;

    private String parentStateRoot;

    /**
     * 区块高度
     */
    private Long height;

    /**
     * 赢票数量
     */
    private Integer winCount;

    /**
     * 父区块基础费率
     */
    private BigDecimal parentBaseFee;

    private Integer forkSignaling;

    /**
     * 消息数
     */
    private Integer messageCount;

    /**
     * 区块产生时间
     */
    private LocalDateTime dateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}