package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 管理员首页-平台概览出参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/5/28 17:26
 **/
@Data
@ApiModel("管理员首页-平台概览出参")
public class SwarmAdminFirstCollectVO {

    @ApiModelProperty(value = "总有效出票数")
    private Long ticketValid;

    @ApiModelProperty(value = "今日有效出票数")
    private Long todayTicketValid;

    @ApiModelProperty(value = "有效节点")
    private Long nodeValid;

    @ApiModelProperty(value = "平台总连接数")
    private Long linkNum;

}
