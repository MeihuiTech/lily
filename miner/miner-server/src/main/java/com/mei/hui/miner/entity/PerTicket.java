package com.mei.hui.miner.entity;

import lombok.Data;

@Data
public class PerTicket {

    private Long totalPerTicketAvail;

    private Long totalPerTicketValid;

    private String walletAddress;
}
