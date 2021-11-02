package com.mei.hui.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ApiModel
public class GetTokenBO {

    @ApiModelProperty(value = "平台分配的用户唯一标识",required = true)
    private String accessKey;

    //默认30分钟
    @ApiModelProperty(value = "token过期时间,默认30分钟")
    private long tokenExpires = 30;
}
