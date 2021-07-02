package com.mei.hui.miner.feign.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.parser.TokenStream;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel("32G扇区gas封装成本")
public class ThirtyTwoGasVO {

    @JSONField(serializeUsing = ToStringSerializer.class)
    @ApiModelProperty("gas费用")
    private BigDecimal gas;

    @JSONField(serializeUsing = ToStringSerializer.class)
    @ApiModelProperty("总成本")
    private BigDecimal cost;

    @JSONField(serializeUsing = ToStringSerializer.class)
    @ApiModelProperty("质押费")
    private BigDecimal pledge;

}
