package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    /** 请求参数 */
    @TableField(exist = false)
    private Map<String, Object> params;

    /*开始时间*/
    @TableField(exist = false)
    private String beginTime;

    /*结束时间*/
    @TableField(exist = false)
    private String endTime;

    /*true 升序，false 降序*/
    @TableField(exist = false)
    private boolean isAsc;

    /*排序字段名称*/
    @TableField(exist = false)
    private String cloumName;

    public Map<String, Object> getParams()
    {
        if (params == null)
        {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }


}
