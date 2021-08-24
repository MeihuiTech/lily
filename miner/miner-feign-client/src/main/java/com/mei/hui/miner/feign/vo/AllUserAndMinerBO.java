package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@ApiModel
@Accessors(chain = true)
public class AllUserAndMinerBO {

    @ApiModelProperty("用户列表")
    private List<FindAllMinerVO> users;

    @ApiModelProperty("游客所使用的userId")
    private Long visitorUserId;

    @ApiModelProperty("游客所使用的userId,对应的用户名称")
    private String userName;
}
