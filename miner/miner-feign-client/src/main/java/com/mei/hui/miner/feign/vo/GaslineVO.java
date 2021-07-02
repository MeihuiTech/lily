package com.mei.hui.miner.feign.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel
public class GaslineVO {

    @JSONField(serializeUsing = ToStringSerializer.class)
    @ApiModelProperty("时间")
    private LocalDateTime date;

    @JSONField(serializeUsing = ToStringSerializer.class)
    @ApiModelProperty("32G扇区gas费用")
    private BigDecimal thirtyTwoGas;

    @JSONField(serializeUsing = ToStringSerializer.class)
    @ApiModelProperty("64G扇区gas费用")
    private BigDecimal sixtyFourGas;

}
