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
 * gas费用聚合
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
@Data
@Accessors(chain = true)
@TableName("fil_report_gas")
public class FilReportGas {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private BigDecimal thirtyTwoGas;

    private BigDecimal thirtyTwoCost;

    private BigDecimal thirtyTwoPledge;

    private BigDecimal sixtyFourGas;

    private BigDecimal sixtyFourCost;

    private BigDecimal sixtyFourPledge;

    private LocalDateTime date;

    private LocalDateTime createTime;


}
