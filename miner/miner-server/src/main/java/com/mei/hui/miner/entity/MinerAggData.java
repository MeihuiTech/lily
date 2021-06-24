package com.mei.hui.miner.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MinerAggData {

    private BigDecimal totalBlockAward;

    private BigDecimal totalPower;

    private Long totalBlocks;
}
