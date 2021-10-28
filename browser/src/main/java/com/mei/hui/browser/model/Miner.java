package com.mei.hui.browser.model;

import lombok.Data;
import java.util.List;

@Data
public class Miner {
    private Long total;

    private List<MinerPower> list;
}
