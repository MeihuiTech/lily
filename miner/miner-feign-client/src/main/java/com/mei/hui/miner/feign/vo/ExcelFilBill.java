package com.mei.hui.miner.feign.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.mei.hui.util.BigDecimalUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ExcelFilBill {

    @ExcelProperty(value = "时间", index = 0)
    private String dateTime;

    @ExcelProperty(value = "消息ID", index = 1)
    private String cid;

    @ExcelProperty(value = "发送方", index = 2)
    private String sender;

    @ExcelProperty(value = "接收方", index = 3)
    private String receiver;

    @ExcelProperty(value = "金额", index = 4)
    private BigDecimal money;

}
