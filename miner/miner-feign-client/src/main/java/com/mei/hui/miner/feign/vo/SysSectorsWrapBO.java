package com.mei.hui.miner.feign.vo;

import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询扇区信息聚合列表入参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/9 16:00
 **/
@Data
@ApiModel("查询扇区信息聚合列表入参")
public class SysSectorsWrapBO extends BasePage {

    @ApiModelProperty(value = "扇区编号")
    private String sectorNo;

    @ApiModelProperty(value = "矿工id")
    private String minerId;

    /** 扇区状态
     0. UnKnown
     1. AP
     2. PC1
     3. PC2
     4. WAITSEED
     5. C1
     6. C2
     7. FIN
     8. PROVING */
    @ApiModelProperty(value = "扇区状态")
    private Integer sectorStatus;

    @ApiModelProperty(value = "开始时间，格式样例：2021-06-12")
    private String beginTime;

    @ApiModelProperty(value = "结束时间，格式样例：2021-06-12")
    private String endTime;

}
