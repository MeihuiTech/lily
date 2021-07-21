package com.mei.hui.miner.feign.vo;

import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel
public class FilBillPageListBO  extends BasePage {

    @ApiModelProperty("旷工id")
    private String minerId;

    @ApiModelProperty("子账号类型")
    private Integer account_type;

    @ApiModelProperty(value = "日期",required = true)
    private String date;

}
