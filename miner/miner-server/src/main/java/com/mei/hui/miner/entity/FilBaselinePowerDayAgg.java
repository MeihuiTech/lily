package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * filcoin 基线和有效算力聚合表，按天聚合
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
@Data
@Accessors(chain = true)
@TableName("fil_baseline_power_day_agg")
public class FilBaselinePowerDayAgg{

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private BigDecimal baseLine;

    private BigDecimal power;

    private Long blocks;

    private LocalDate date;

    private LocalDateTime createTime;


}
