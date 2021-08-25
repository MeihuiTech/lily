package com.mei.hui.user.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ApiModel
@Accessors(chain = true)
public class VisitRoleBO {

    @ApiModelProperty("角色状态（0正常 1停用）")
    private Integer state;
}
