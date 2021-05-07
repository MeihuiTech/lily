package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 矿机信息对象 sys_machine_info
 * 
 * @author ruoyi
 * @date 2021-03-02
 */
@Data
@TableName("sys_machine_info")
public class SysMachineInfo
{
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 矿工hash */
    private String minerId;

    /** 主机名 */
    private String hostname;

    /** cpu型号 */
    private String cpuModel;

    /** cpu核数 */
    private Long cpuCore;

    /** 内存使用量, 单位GB */
    private Long memoryUsed;

    /** 内存总大小, 单位GB */
    private Long memoryTotal;

    /** gpu型号,多个型号逗号分隔 */
    private String gpuModel;

    /** gpu数量 */
    private Long gpuCount;

    /** 磁盘使用量,单位TB */
    private Long diskUsed;

    /** 磁盘总大小, 单位TB */
    private Long diskTotal;

    /** 机器类型 */
    private String machineType;

    /** worker数量 */
    private Long workerCount;

    private Integer online;

}
