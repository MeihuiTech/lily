package com.mei.hui.miner.model;

import java.math.BigDecimal;

public class PoolEarningVo {
    public BigDecimal getTotalEarning() {
        return totalEarning;
    }

    public void setTotalEarning(BigDecimal totalEarning) {
        this.totalEarning = totalEarning;
    }

    public BigDecimal getTodayEarning() {
        return todayEarning;
    }

    public void setTodayEarning(BigDecimal todayEarning) {
        this.todayEarning = todayEarning;
    }

    private BigDecimal totalEarning;
    private BigDecimal todayEarning;
}
