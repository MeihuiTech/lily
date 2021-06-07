package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/7 14:32
 **/
@Data
@ApiModel("管理员-用户列表参数")
public class UserMinerBO {

    @ApiModelProperty(value = "userId的list")
    private List<Long> userIds = new ArrayList<>();

    @ApiModelProperty(value = "true 升序，false 降序")
    private boolean isAsc;

    @ApiModelProperty(value = "排序字段名称")
    private String cloumName;


}
