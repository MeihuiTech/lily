package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 上传/下载宽带信息出参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/10 17:51
 **/
@Data
@ApiModel(value = "上传/下载宽带信息出参")
public class BroadbandUpDownVO {


    @ApiModelProperty(value = "秒级时间戳")
    private Long timestamp;

    /**
     * bps（bits per second）是数据传输速率的常用单位，意思是比特率、比特/秒、位/秒、每秒传的位数。bps=bits/s，  1 byte (B) = 8 bits (b) 字节=8个二进制位
     */
    @ApiModelProperty(value = "数值，单位 bps")
    private BigDecimal value;

}
