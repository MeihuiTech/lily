package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel
public class ReportDeadlinesBO {

    @ApiModelProperty(value = "矿工id",required = true)
    private String minerId;

    @ApiModelProperty(value = "窗口列表",required = true)
    private List<WindowBo> windows = new ArrayList<>();
}
