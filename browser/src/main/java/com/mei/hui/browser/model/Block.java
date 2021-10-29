package com.mei.hui.browser.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Block {

    //数据总数
    private long count;

    //全网24小时出块奖励增量
    private BigDecimal twentyFourTotalBlockAward;

    private List<MinerAndBlock> list;
}
