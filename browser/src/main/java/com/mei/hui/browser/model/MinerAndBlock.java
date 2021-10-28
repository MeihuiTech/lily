package com.mei.hui.browser.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MinerAndBlock {

    private String minerId;

    //排序
    private long sort;

    private int blockCount;
}
