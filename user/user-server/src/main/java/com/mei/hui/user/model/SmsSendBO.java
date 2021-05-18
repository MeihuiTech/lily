package com.mei.hui.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SmsSendBO {
    @ApiModelProperty(value = "业务名称,输入字符串",required = true)
    private String serviceName;
}
