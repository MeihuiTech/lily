package com.mei.hui.miner.feign.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * fil账单上报入参
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/9/13 17:05
 **/
@Data
public class FilBillReportListBO {


    /**
     * 矿工id，minerId
     */
    private String miner;

    /**
     * 日期，补录的是date前一天的数据，格式：2020-09-09
     */
    private String date;

    /**
     * 是否补录数据：true补录数据，false不用补录数据，正常账单数据
     */
    private boolean firstTipSet;

    /**
     * 结余
     */
    private BigDecimal balance;

    /**
     * 账单具体消息list
     */
    private List<FilBillReportBO> messages;
}
