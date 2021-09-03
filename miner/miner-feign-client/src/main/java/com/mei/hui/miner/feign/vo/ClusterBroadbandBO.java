package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@ApiModel
@Accessors(chain = true)
public class ClusterBroadbandBO {

    @ApiModelProperty(value = "集群名称")
    private String clusterName;

    @ApiModelProperty(value = "上传宽带信息出参")
    private List<BroadbandUpDownVO> broadbandUpVOList;

    @ApiModelProperty(value = "下载宽带信息出参")
    private List<BroadbandUpDownVO> broadbandDownVOList;
}
