package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 对外API-全网数据
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/3 17:31
 **/
@Data
@ApiModel("对外API-全网数据")
public class ForeignNetworkVO {


    @ApiModelProperty("全网数据")
    private NetWordDataVo netWordData;

    @ApiModelProperty("32G扇区封装成本")
    private ThirtyTwoGasVO thirtyTwoGasVO;

    @ApiModelProperty("64G扇区封装成本")
    private SixtyFourGasVO sixtyFourGasVO;

}
