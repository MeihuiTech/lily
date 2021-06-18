package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkVO {

    @ApiModelProperty(value = "连接数",required = true)
    private long linkNum;

    @ApiModelProperty(value = "时间",required = true)
    private LocalDateTime dateTime;

}
