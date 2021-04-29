package com.mei.hui.miner.entity;

public class SysTotalEarning {

    /** 总收益 */
    private double totalEarning;

    /** 总锁仓收益 */
    private double totalLockAward;

    public SysTotalEarning() {
    }

    public SysTotalEarning(double totalEarning, double totalLockAward) {
        this.totalEarning = totalEarning;
        this.totalLockAward = totalLockAward;
    }

    public double getTotalEarning() {
        return totalEarning;
    }

    public void setTotalEarning(double totalEarning) {
        this.totalEarning = totalEarning;
    }

    public double getTotalLockAward() {
        return totalLockAward;
    }

    public void setTotalLockAward(double totalLockAward) {
        this.totalLockAward = totalLockAward;
    }

    @Override
    public String toString() {
        return "SysTotalEarning{" +
                "totalEarning=" + totalEarning +
                ", totalLockAward=" + totalLockAward +
                '}';
    }
}
