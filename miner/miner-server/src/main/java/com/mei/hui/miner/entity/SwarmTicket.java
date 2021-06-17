package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 节点出票信息
 */
@Data
@TableName("swarm_ticket")
public class SwarmTicket {
    private Long id;

    /**
     * 节点ip
     */
    private String nodeIp;

    /**
     * 节点port
     */
    private Integer nodePort;

    /**
     * 类型:0-无效;1-有效
     */
    private Integer type;

    /**
     * 状态:0-未兑换；1-兑换成功；2-兑换失败；3-兑换中
     */
    private Integer state;

    /**
     * 票的面值
     */
    private BigDecimal parValue;

    private String peer;

    private String beneficiary;

    private String chequebook;

    private String hash;

    private Date createTime;

}