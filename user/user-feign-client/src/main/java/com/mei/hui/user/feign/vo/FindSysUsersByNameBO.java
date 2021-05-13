package com.mei.hui.user.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel
public class FindSysUsersByNameBO {
    @ApiModelProperty("用户名称")
    private String name;
}
