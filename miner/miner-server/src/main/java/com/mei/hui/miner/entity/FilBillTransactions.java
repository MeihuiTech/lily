package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * FIL币账单转账信息表
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/3 16:46
 **/
@Data
@TableName("fil_bill_transactions")
public class FilBillTransactions {


    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * FIL币账单表id
     */
    private Long filBillId;

    /**
     * 发送方
     */
    private String sender;

    /**
     * 接收方
     */
    private String receiver;

    /**
     * 金额
     */
    private BigDecimal money;

    /**
     * 类型：0Node Fee矿工手续费，1Burn Fee燃烧手续费，2Transfer转账，3BlockAward区块奖励，4Other其它
     */
    private Integer type;

    /**
     * 交易类型：0内部交易，1外部交易
     */
    private Integer transactionType;

    /**
     * 外部交易的收支：0支出，1收入
     */
    private Integer outsideType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
