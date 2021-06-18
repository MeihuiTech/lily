package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("swarm_one_day_agg")
public class SwarmOneDayAgg {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 节点唯一标识
     */
    private String peerId;

    /**
     * 日期
     */
    private LocalDateTime date;

    private LocalDateTime createTime;

    private Long perTicketAvail;

    private Long perTicketValid;
}
