package com.mei.hui.miner.feign.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel(value = "全网基线算力走势图")
public class BaselineAndPowerVO {


    @JSONField(serializeUsing = ToStringSerializer.class)
    @ApiModelProperty("基线值")
    private BigDecimal baseLine;

    @JSONField(serializeUsing = ToStringSerializer.class)
    @ApiModelProperty("全网有效算力")
    private BigDecimal power;

    @ApiModelProperty("日期")
    private LocalDate date;

}
