package com.mei.hui.miner.feign.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 矿工列表导出excel
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/10 19:34
 **/
@Data
public class MinerInfoExportExcelVO extends BaseRowModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * value: 表头名称
     * index: 列的号, 0表示第一列
     */
    @ExcelProperty(value = "矿工ID",index = 0)
    private String minerId;

    /**
     * 有效算力, 单位B
     */
    @ExcelProperty(value = "有效算力",index = 1)
    private String powerAvailableAndUnit;

//    @ExcelProperty(value = "有效算力单位",index = 0)
//    private String powerAvailableUnit;

    /**
     * 算力增速, 单位B
     */
    @ExcelProperty(value = "算力增速",index = 2)
    private BigDecimal powerIncreasePerDay;

    /**
     * 当天出块份数
     */
    @ExcelProperty(value = "今日已出块份数",index = 3)
    private Long blocksPerDay;

    /**
     * 矿工可用余额,单位FIL
     */
    @ExcelProperty(value = "可用余额(FIL)",index = 4)
    private BigDecimal balanceMinerAvailable;

    @ExcelProperty(value = "Worker账户余额(FIL)",index = 5)
    private BigDecimal balanceWorkerAccount;

    @ExcelProperty(value = "PoSt账户余额(FIL)",index = 6)
    private BigDecimal postBalance;

    @ExcelProperty(value = "有效/错误扇区",index = 7)
    private String sectorAvailableAndError;

//    @ExcelProperty(value = "错误状态扇区数量",index = 0)
//    private Integer sectorError;

    @ExcelProperty(value = "矿机数量(在线/离线)",index = 8)
    private String onlineMachineCountAndOff;

//    @ExcelProperty(value = "离线矿机数量",index = 0)
//    private Long offMachineCount;


}
