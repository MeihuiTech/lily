package com.mei.hui.miner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mei.hui.miner.entity.SysMachineInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.Map;

@ApiModel
public class RequestMachineInfo extends SysMachineInfo {
    @ApiModelProperty(value = "id")
    @JsonIgnore
    private Integer id;

    @ApiModelProperty(value = "矿工ID", required = true)
    private Integer minerId;

    @ApiModelProperty(value = "hostname", required = true)
    private String hostname;

    @JsonIgnore
    private Date createTime;

    @JsonIgnore
    private Date updateTime;

    @JsonIgnore
    private String createBy;

    @JsonIgnore
    private String updateBy;

    @JsonIgnore
    /** 请求参数 */
    private Map<String, Object> params;
}
