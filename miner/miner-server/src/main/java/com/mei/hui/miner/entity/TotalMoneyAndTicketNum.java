package com.mei.hui.miner.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TotalMoneyAndTicketNum {
    private BigDecimal totalMoney;
    private BigDecimal totalTicketValid;
    private BigDecimal totalTicketAvail;
    private BigDecimal totalLinkNum;
    private BigDecimal totalSize;
    private BigDecimal onlineNodeNum;
}
