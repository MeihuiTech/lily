package com.mei.hui.miner.feign.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FindChart {

    private Long totallinkNum;

    private Long totalTicketValid;

    private BigDecimal totalConvertBzz;

    private LocalDateTime date;
}
