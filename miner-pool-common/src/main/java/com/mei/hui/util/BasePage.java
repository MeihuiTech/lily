package com.mei.hui.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class BasePage {

    @ApiModelProperty(value = "当前页码",required = true)
    private long pageNum = 1;

    @ApiModelProperty(value = "每页数量",required = true)
    private long pageSize = 10;

    @ApiModelProperty(value = "true 升序，false 降序")
    private boolean isAsc;

    @ApiModelProperty(value = "排序字段名称")
    private String cloumName;

}
