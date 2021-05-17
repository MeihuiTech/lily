package com.mei.hui.miner.entity;



import lombok.Data;

import java.math.BigDecimal;

@Data
public class AggMiner {

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
