package com.mei.hui.miner.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class AggWithdrawVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("用户主键")
    private Long sysUserId;

    @ApiModelProperty("用户名称")
    private String userName;

    private BigDecimal takeTotalMony;

    private BigDecimal totalFee;

    private Integer tatalCount;
}
