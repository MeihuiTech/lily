package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("miner_longitude_latitude")
public class MinerLongitudeLatitude {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String minerId;

    private String ip;


    /**
     * 地址
     */
    private String address;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 矿工类型，0不是我们的，1是我们的，默认0
     */
    private Integer type;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}