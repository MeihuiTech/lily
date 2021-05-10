package com.mei.hui.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class ChangeAccountInput {

    @ApiModelProperty(value = "用户id",required = true)
    private Long userId;
}
