package com.mei.hui.miner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mei.hui.miner.entity.SysSectorInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@ApiModel
@Data
public class RequestSectorInfo extends SysSectorInfo {

    @ApiModelProperty(value = "主机名", required = true)
    private String hostname;

    @ApiModelProperty(value = "扇区编号", required = true)
    private Long sectorNo;

    @ApiModelProperty(value = "扇区大小", required = true)
    private Long sectorSize;

    @ApiModelProperty(value = "扇区状态", required = true)
    private Integer sectorStatus;

    @ApiModelProperty(value = "扇区当前状态持续时间", required = true)
    private Long sectorDuration;


    private LocalDateTime createTime;


    private LocalDateTime updateTime;


    private String createBy;


    private String updateBy;


    /** 请求参数 */
    private Map<String, Object> params;
}