package com.mei.hui.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class BasePage {

    @ApiModelProperty(value = "当前页码",required = true)
    private int pageNum = 1;

    @ApiModelProperty(value = "每页数量",required = true)
    private int pageSize = 10;

}
