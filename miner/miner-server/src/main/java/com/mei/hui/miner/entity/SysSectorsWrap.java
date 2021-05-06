package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 扇区信息聚合对象 sys_sectors_wrap
 * 
 * @author ruoyi
 * @date 2021-03-04
 */
@Data
@TableName("sys_sectors_wrap")
public class SysSectorsWrap
{

    @TableId(type = IdType.AUTO)
    private Long id;


    private String minerId;


    private String hostname;

    private Long sectorNo;


    private Long sectorSize;

    /** 扇区状态
0. UnKnown
1. AP
2. PC1
3. PC2
4. WAITSEED
5. C1
6. C2
7. FIN
8. PROVING */

    private Integer sectorStatus;

    /** 扇区状态持续时间，单位秒(s) */
    private Long sectorDuration;

    private String createBy;

    private String updateBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private long pageNum = 1;

    @TableField(exist = false)
    private long pageSize = 10;


}
