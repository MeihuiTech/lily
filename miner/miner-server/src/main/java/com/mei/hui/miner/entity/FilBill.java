package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * FIL币账单
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-21
 */
@Data
@Accessors(chain = true)
@TableName("fil_bill")
public class FilBill implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 旷工id
     */
    private String minerId;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 区块高度
     */
    private Long blockHeight;

    /**
     * 发送方
     */
    private String sender;

    /**
     * 接收方
     */
    private String receiver;

    /**
     * 方法
     */
    private String method;

    /**
     * 金额
     */
    private BigDecimal money;

    /**
     * 0-转出;1-转入
     */
    private Integer type;

    /**
     * 状态
     */
    private String state;

    /**
     * 0-Owner；1-Worker；2-Controller
     */
    private String accountType;

    /**
     * 账单产生时间
     */
    private Date dateTime;

    /**
     * 创建时间
     */
    private Date createTime;


}
