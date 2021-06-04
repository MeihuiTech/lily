package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("管理员-矿池收益-根据币种分别显示“总手续费”和“今日手续费” 总出参")
public class PoolEarningVo {

    @ApiModelProperty(value = "总手续费收益")
    private List<TransferRecordFeeVO> allTransferRecordFeeVOList;

    @ApiModelProperty(value = "今日手续费收益")
    private List<TransferRecordFeeVO> todayTransferRecordFeeVOList;

}
