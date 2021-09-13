package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 非平台矿工,仅用于大屏显示
 * </p>
 *
 * @author 鲍红建
 * @since 2021-09-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NoPlatformMiner implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 矿工ID
     */
    private String minerId;

    /**
     * 有效算力, 单位B
     */
    private BigDecimal powerAvailable;

    /**
     * 总资产
     */
    private Double balanceMinerAccount;

    /**
     * 累计出块份数
     */
    private Long totalBlocks;

    /**
     * worker账户余额
     */
    private Double workerBalance;

    /**
     * post账户余额
     */
    private Double postBalance;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
