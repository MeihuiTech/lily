package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@ApiModel("汇总")
public class BillMethodTotalVO {

    @ApiModelProperty("方法对应的金额实体List")
    private List<BillMethodMoneyVO> billMethodMoneyVOList;

    @ApiModelProperty("总额")
    private BigDecimal total;
}
