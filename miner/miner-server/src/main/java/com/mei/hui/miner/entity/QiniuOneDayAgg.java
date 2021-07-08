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
import java.util.Date;

/**
 * <p>
 * 七牛存储使用容量聚合表
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-08
 */
@Data
@Accessors(chain = true)
@TableName("qiniu_one_day_agg")
public class QiniuOneDayAgg {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "累计使用容量,单位Byte")
    private BigDecimal storeSize;

    @ApiModelProperty(value = "创建日期")
    private LocalDate createDate;


}
