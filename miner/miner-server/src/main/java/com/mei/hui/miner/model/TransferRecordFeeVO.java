package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/3 17:41
 **/
@Data
@ApiModel("管理员-矿池收益-根据币种分别显示“总手续费”和“今日手续费” 出参")
public class TransferRecordFeeVO {

    @ApiModelProperty(value = "平台收取手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "货比种类,FIL,CHIA")
    private String name;

}
