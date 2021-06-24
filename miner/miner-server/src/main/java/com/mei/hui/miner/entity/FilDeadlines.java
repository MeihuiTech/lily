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
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * filcoin 矿工窗口记录表
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
@Data
@Accessors(chain = true)
@TableName("fil_report_deadlines")
public class FilDeadlines {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer deadline;

    private Integer partitions;

    private Integer sectors;

    private Integer sectorsFaults;

    private Long provenPartitions;

    private Integer isCurrent;

    private Long sort;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
