package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 宽带信息出参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/10 17:49
 **/
@Data
@ApiModel(value = "宽带信息出参")
public class BroadbandVO {

    @ApiModelProperty(value = "上传宽带信息出参")
    private List<BroadbandUpDownVO> broadbandUpVOList;

    @ApiModelProperty(value = "下载宽带信息出参")
    private List<BroadbandUpDownVO> broadbandDownVOList;

}
