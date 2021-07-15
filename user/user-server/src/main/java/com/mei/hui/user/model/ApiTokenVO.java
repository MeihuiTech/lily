package com.mei.hui.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel
public class ApiTokenVO {

    @ApiModelProperty("api token")
    private String token;
}
