package com.mei.hui.user.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@ApiModel
@Data
public class FindSysUserListInput {

    @ApiModelProperty(value = "userId 集合",required = true)
    private List<Long> userIds = new ArrayList();
}
