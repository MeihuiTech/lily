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
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 全网数据上报记录表
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
@Data
@Accessors(chain = true)
@TableName("fil_report_network_data")
public class FilReportNetworkData {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private BigDecimal totalBlockAward;

    private BigDecimal power;

    private Long blocks;

    private Long blockHeight;

    private Long activeMiner;

    private LocalDateTime updateTime;


}
