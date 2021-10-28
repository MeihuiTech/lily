package com.mei.hui.browser.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MinerPower {

    private String minerId;
    //排序
    private long sort;
    private BigDecimal powerAvailable;

}
