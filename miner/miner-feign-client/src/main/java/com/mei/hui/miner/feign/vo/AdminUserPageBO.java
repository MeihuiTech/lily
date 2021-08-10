package com.mei.hui.miner.feign.vo;

import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel
public class AdminUserPageBO {

    @ApiModelProperty("管理员id")
    private Long adminId;

    @ApiModelProperty("管理员名称")
    private String adminName;


    @ApiModelProperty("矿工用户列表")
    List<GeneralUserBO> list = new ArrayList<>();

}
