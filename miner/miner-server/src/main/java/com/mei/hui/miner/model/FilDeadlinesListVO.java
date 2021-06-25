package com.mei.hui.miner.model;

import com.mei.hui.miner.entity.FilDeadlines;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/25 16:20
 **/
@Data
@ApiModel("filcoin 矿工窗口记录表出参")
public class FilDeadlinesListVO {

    @ApiModelProperty(value = "窗口序号")
    private Integer deadline;

    @ApiModelProperty(value = "扇区数量")
    private Integer sectors;

    @ApiModelProperty(value = "错误扇区数量")
    private Integer sectorsFaults;

    @ApiModelProperty(value = "是否是当前窗口,1-是当前窗口，0-不是")
    private Integer isCurrent;

    @ApiModelProperty(value = "轮数")
    private Long sort;


}
