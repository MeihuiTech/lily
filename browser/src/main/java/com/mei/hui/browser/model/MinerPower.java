package com.mei.hui.browser.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MinerPower {

    private String minerId;

    private BigDecimal powerAvailable;

}
