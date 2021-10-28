package com.mei.hui.browser.model;

import lombok.Data;

import java.util.List;

@Data
public class Block {

    //数据总数
    private long count;

    private List<MinerAndBlock> list;
}
