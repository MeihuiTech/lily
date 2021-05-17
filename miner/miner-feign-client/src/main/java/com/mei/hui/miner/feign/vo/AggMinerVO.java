package com.mei.hui.miner.feign.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AggMinerVO {
    /**
     * 总算力
     */
    private BigDecimal powerAvailable;

    /**
     * 总收益
     */
    private BigDecimal totalBlockAward;


    private Long userId;

}
