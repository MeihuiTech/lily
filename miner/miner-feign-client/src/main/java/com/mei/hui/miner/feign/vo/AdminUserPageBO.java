package com.mei.hui.miner.feign.vo;

import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel
public class AdminUserPageBO extends BasePage {

    @ApiModelProperty("管理员id")
    private Long adminId;

    @ApiModelProperty("管理员名称")
    private String adminName;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("用户名称")
    private String userName;

}
