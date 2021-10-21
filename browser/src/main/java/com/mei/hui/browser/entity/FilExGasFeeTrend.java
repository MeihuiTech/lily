package com.mei.hui.browser.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扇区封装Gas费用
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/19 17:19
 **/
@Data
@TableName("fil_ex_gas_fee_trend")
public class FilExGasFeeTrend {

    /**
     * 高度
     */
    private Long height;

    /**
     * filecoin时间戳
     */
    private Long timestamp;

    /**
     * 32GB存储封Gas消耗(Fil/TiB)
     */
    private BigDecimal thirtyTwoGas;

    /**
     * 64GB存储封Gas消耗(Fil/TiB)
     */
    private BigDecimal sixtyFourGas;

    /**
     * 扇区质押量(Fil/TiB)
     */
    private BigDecimal sectorPledge;

    /**
     * 当前基础费率(autoFil)
     */
    private BigDecimal baseFee;

    private Long createdAt;

    private Long updatedAt;


}
