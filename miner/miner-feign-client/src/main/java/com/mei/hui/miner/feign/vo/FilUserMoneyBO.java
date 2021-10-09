package com.mei.hui.miner.feign.vo;

import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 管理员-用户收益-用户列表入参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/17 13:50
 **/
@Data
@ApiModel(value = "管理员-用户收益-用户收益列表")
public class FilUserMoneyBO extends BasePage {

    @ApiModelProperty(value = "用户Id")
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

}
