package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * FIL币账单消息详情表
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

//    @TableId(value = "id", type = IdType.AUTO)
    private String id;

   /* @TableField(exist = false)*/
//    private List<FilBillDetail> detailList;

    /**
     * 矿工id
     */
    private String minerId;

    /**
     * 消息id
     */
    private String cid;

    /**
     * 区块高度
     */
    private Long height;

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
     * 类型：0账单消息，1区块奖励
     */
    private Integer type;

    /**
     * 账单产生时间
     */
    private LocalDateTime dateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
