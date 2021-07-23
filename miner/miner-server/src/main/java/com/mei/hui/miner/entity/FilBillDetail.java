package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * FIL币账单明细
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-21
 */
@Data
@Accessors(chain = true)
@TableName("fil_bill_detail")
public class FilBillDetail implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账单id
     */
    private Long billId;

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
     * 0-销毁手续费;1-节点手续费;2-转账
     */
    private Integer type;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
