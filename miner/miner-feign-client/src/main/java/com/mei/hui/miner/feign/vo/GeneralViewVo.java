package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel("概览")
public class GeneralViewVo {


   @ApiModelProperty("区块高度")
   private Long blockHeight;

    @ApiModelProperty("全网数据")
    private NetWordDataVo netWordData;

    @ApiModelProperty("平台数据")
    private PlatformDataVo platformData;

    @ApiModelProperty("32G扇区封装成本")
    private ThirtyTwoGasVO thirtyTwoGasVO;

    @ApiModelProperty("64G扇区封装成本")
    private SixtyFourGasVO sixtyFourGasVO;
}
