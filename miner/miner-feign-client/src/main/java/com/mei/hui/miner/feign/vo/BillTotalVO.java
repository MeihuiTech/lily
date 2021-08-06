package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel("fil币账单汇总出参")
public class BillTotalVO {


    @ApiModelProperty("收入信息汇总")
    private BillMethodTotalVO in;

    @ApiModelProperty("支出信息汇总")
    private BillMethodTotalVO out;


}
