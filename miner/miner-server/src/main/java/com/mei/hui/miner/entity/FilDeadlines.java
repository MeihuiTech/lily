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

    private String minerId;

    /**
     * 窗口序号
     */
    private Integer deadline;

    private Integer partitions;

    /**
     * 扇区数量
     */
    private Integer sectors;

    /**
     * 错误扇区数量
     */
    private Integer sectorsFaults;

    private Long provenPartitions;

    /**
     * 是否是当前窗口,1-是当前窗口，0-不是
     */
    private Integer isCurrent;

    /**
     * 轮数
     */
    private Long sort;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
