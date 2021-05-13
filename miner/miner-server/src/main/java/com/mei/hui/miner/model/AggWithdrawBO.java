package com.mei.hui.miner.model;

import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel
public class AggWithdrawBO extends BasePage {

    @ApiModelProperty("用户名称")
    private String userName;


    @ApiModelProperty(value = "true 升序，false 降序")
    private boolean isAsc;

    @ApiModelProperty(value = "排序字段名称")
    private String cloumName;

}
