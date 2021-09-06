package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@TableName("fil_baseline_power_hour_agg")
public class FilBaselinePowerHourAgg {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long blocks;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    private LocalDateTime createTime;


}
