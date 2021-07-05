package com.mei.hui.miner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mei.hui.miner.entity.FilMinerControlBalance;
import com.mei.hui.miner.entity.SysMinerInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@ApiModel
public class RequestMinerInfo extends SysMinerInfo {
    @ApiModelProperty(value = "id", required = false)
    @JsonIgnore
    private Integer id;

    @ApiModelProperty(value = "用户ID", required = true)
    private Integer userId;

    @ApiModelProperty(value = "矿工ID", required = true)
    private String minerId;

    @JsonIgnore
    private Date createTime;

    @JsonIgnore
    private Date updateTime;

    @JsonIgnore
    private String createBy;

    @JsonIgnore
    private String updateBy;

    //有效扇区
    private Integer sectorActive;

    @JsonIgnore
    /** 请求参数 */
    private Map<String, Object> params;

    @ApiModelProperty(value = "FilMinerControlBalance表的list")
    private List<FilMinerControlBalance> controlAccounts;

    public List<FilMinerControlBalance> getControlAccounts() {
        return controlAccounts;
    }

    public void setControlAccounts(List<FilMinerControlBalance> controlAccounts) {
        this.controlAccounts = controlAccounts;
    }

    public Integer getSectorActive() {
        return sectorActive;
    }

    public void setSectorActive(Integer sectorActive) {
        this.sectorActive = sectorActive;
    }
}
