package com.mei.hui.browser.model;

import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class RankingBO extends BasePage {
    @ApiModelProperty(required = true,value = "0--24小时,1--7天,2--30天,3--1年")
    private int range;
}
