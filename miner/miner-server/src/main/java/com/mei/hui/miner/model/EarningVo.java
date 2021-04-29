package com.mei.hui.miner.model;

import lombok.Data;

@Data
public class EarningVo {
    /** 总收益 */
    private double totalEarning;

    /** 总锁仓收益 */
    private double totalLockAward;

    /** 用户总共已提取 */
    private double totalWithdraw;

    /** 用户可提取金额 */
    private double availableEarning;

    public EarningVo() {
    }

    public EarningVo(double totalEarning, double totalLockAward, double totalWithdraw, double availableEarning) {
        this.totalEarning = totalEarning;
        this.totalLockAward = totalLockAward;
        this.totalWithdraw = totalWithdraw;
        this.availableEarning = availableEarning;
    }


}
