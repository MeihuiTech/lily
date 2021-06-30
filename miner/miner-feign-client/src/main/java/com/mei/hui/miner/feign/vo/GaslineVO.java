package com.mei.hui.miner.feign.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel
public class GaslineVO {

    @ApiModelProperty("时间")
    private LocalDateTime date;

    @ApiModelProperty("32G扇区gas费用")
    private BigDecimal thirtyTwoGas;

    @ApiModelProperty("64G扇区gas费用")
    private BigDecimal sixtyFourGas;

}
