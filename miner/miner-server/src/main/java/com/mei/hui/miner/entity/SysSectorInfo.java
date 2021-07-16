package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 扇区封装记录表 sys_sector_info
 * 
 * @author ruoyi
 * @date 2021-03-04
 */
@Data
@TableName("sys_sector_info")
public class SysSectorInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 矿工hash
     */

    private String minerId;

    /**
     * 主机名
     */
    private String hostname;

    /**
     * 扇区编号
     */
    private Long sectorNo;

    /**
     * 扇区大小, 单位GB
     */
    private Long sectorSize;

    /**
     * 扇区状态
     * 0. UnKnown
     * 1. AP
     * 2. PC1
     * 3. PC2
     * 4. WAITSEED
     * 5. C1
     * 6. C2
     * 7. FIN
     * 8. PROVING
     */
    private Integer sectorStatus;

    /**
     * 扇区当前状态开始时间，获取不到可传空
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime sectorStart;

    /**
     * 扇区当前状态结束时间，获取不到可传空
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime sectorEnd;

    /**
     * 扇区当前状态持续时间，单位秒(s)
     */
    private Long sectorDuration;

    /**
     * 封装状态：0进行中，1已完成，默认0
     */
    private Integer status;

    private String createBy;

    private String updateBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private long pageNum = 1;

    @TableField(exist = false)
    private long pageSize = 10;

}


