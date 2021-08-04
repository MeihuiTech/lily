package com.mei.hui.user.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class VisitorLoginBO {

    @ApiModelProperty(value = "游客类型,0-普通用户(默认值)；1-普通管理员",required = true)
    private int visitorType = 0;
}
