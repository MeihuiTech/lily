package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户按天聚合对象 sys_agg_account_daily
 * @author ruoyi
 * @date 2021-04-06
 */
@Data
@TableName("sys_agg_account_daily")
public class SysAggAccountDaily
{
    @TableId(type= IdType.AUTO)
    private Long id;

    private String date;

    private String minerId;

    /** 锁仓收益 */
    private BigDecimal lockAward;

    /** 扇区质押 */
    private BigDecimal sectorPledge;

    /** 账户余额 */
    private BigDecimal balanceAccount;

    /** 可用余额 */
    private BigDecimal balanceAvailable;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createBy;

}
