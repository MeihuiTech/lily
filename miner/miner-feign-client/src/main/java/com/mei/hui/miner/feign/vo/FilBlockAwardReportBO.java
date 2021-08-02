package com.mei.hui.miner.feign.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/30 19:46
 **/
@Data
public class FilBlockAwardReportBO {


    /**
     * 矿工id，上报接口提供，可能上报账单的时候矿工id在矿工表有可能不存在,minerId
     */
    private String miner;

    /**
     * 消息id,messageId
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
    private Long timestamp;


}
