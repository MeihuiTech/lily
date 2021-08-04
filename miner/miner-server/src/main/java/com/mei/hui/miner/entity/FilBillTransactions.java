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
     * 类型：Node Fee节点手续费，Burn Fee销毁手续费，Transfer转账
     */
    private String type;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
