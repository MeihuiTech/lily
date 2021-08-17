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
     * 类型：0Node Fee矿工手续费，1Burn Fee燃烧手续费，2Transfer转账，3BlockAward区块奖励
     */
    private String type;


}
