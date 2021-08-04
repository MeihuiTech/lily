package com.mei.hui.miner.feign.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * FIL币账单上报入参
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/30 19:08
 **/
@Data
public class FilBillReportBO {


    /**
     * 旷工id，minerId
     */
    private String miner;

    /**
     * 消息id
     */
    private String cid;

    /**
     * 区块高度
     */
    private Long height;

    /**
     * 发送方，sender
     */
    private String from;

    /**
     * 接收方，receiver
     */
    private String to;

    /**
     * 方法
     */
    private String method;

    /**
     * 金额,money
     */
    private BigDecimal value;

    /**
     * 字节大小
     */
    private Long sizeBytes;

    /**
     * 标记
     */
    private Long nonce;

    /**
     * 手续费率上限
     */
    private BigDecimal gasFeeCap;

    /**
     * 节点小费费率
     */
    private BigDecimal gasPremium;

    /**
     * Gas用量上限
     */
    private Long gasLimit;

    /**
     * 状态根
     */
    private String stateRoot;

    /**
     * 状态码
     */
    private Long exitCode;

    /**
     * Gas实际用量
     */
    private Long gasUsed;

    /**
     * 父区块基础费率
     */
    private BigDecimal parentBaseFee;

    /**
     * 燃烧基础费用
     */
    private BigDecimal baseFeeBurn;

    /**
     * 溢出部分燃烧费用
     */
    private BigDecimal overEstimationBurn;

    /**
     * 矿工罚金
     */
    private BigDecimal minerPenalty;

    /**
     * 矿工小费
     */
    private BigDecimal minerTip;

    /**
     * 退款
     */
    private BigDecimal refund;

    /**
     * gas退款
     */
    private Long gasRefund;

    /**
     * 烧掉的gas费
     */
    private Long gasBurned;

    /**
     * 账单产生时间，dateTime
     */
    private Long timestamp;

    /**
     * 参数，params
     */
    private String params;

    /**
     * FIL币账单转账信息表list
     */
    private List<FilBillTransactionsReportBO> transaction;


}
