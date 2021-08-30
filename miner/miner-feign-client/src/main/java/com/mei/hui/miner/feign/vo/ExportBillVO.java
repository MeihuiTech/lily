package com.mei.hui.miner.feign.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ExportBillVO {

    @ExcelProperty(value = "日期", index = 0)
    private String date;

    @ExcelProperty(value = "收入", index = 1)
    private BigDecimal inMoney;

    @ExcelProperty(value = "支出", index = 2)
    private BigDecimal outMoney;

    @ExcelProperty(value = "结余", index = 3)
    private BigDecimal balance;

}
