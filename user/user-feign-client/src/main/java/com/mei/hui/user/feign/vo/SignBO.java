package com.mei.hui.user.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SignBO {

    @ApiModelProperty(value = "token",required = true)
   private String token;
}
