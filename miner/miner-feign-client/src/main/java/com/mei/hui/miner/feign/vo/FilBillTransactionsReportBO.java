package com.mei.hui.miner.feign.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * FIL币账单转账信息表
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/3 16:46
 **/
@Data
public class FilBillTransactionsReportBO {

    /**
     * 发送方，sender
     */
    private String from;

    /**
     * 接收方，receiver
     */
    private String to;

    /**
     * 金额,money
     */
    private BigDecimal value;

    /**
     * 类型：Miner Fee存储提供者手续费，Burn Fee销毁手续费，Transfer转账
     */
    private String type;


}
