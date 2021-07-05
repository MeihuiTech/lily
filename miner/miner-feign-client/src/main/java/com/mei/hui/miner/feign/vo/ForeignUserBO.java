package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 对外API-用户数据入参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/5 11:55
 **/
@Data
@ApiModel("对外API-用户数据入参")
public class ForeignUserBO {

    @ApiModelProperty(value = "用户apiKey")
    private String apiKey;

}
