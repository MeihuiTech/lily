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
@ApiModel
public class BillOutVO {

    @ApiModelProperty("方法对应的金额")
    private List<Map<String,Object>> map;

    @ApiModelProperty("其他转出")
    private BigDecimal other;

    @ApiModelProperty("支出总额")
    private BigDecimal total;
}
