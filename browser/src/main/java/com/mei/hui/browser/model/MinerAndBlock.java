package com.mei.hui.browser.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class MinerAndBlock {

    private String minerId;

    //排序
    private long sort;

    //矿工24小时出块奖励增量
    private BigDecimal twentyFourBlockAward;

    private int blockCount;
}
