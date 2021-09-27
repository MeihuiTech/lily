package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.entity.FilBillDayAgg;
import com.mei.hui.miner.entity.FilBillTransactions;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.util.Result;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * FIL币账单 服务类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-21
 */
public interface FilBillService extends IService<FilBill> {


    /**
     * 账单方法下拉列表
     * @param filBillMethodBO
     * @return
     */
    public List<String> selectFilBillMethodList(FilBillMethodBO filBillMethodBO);

    /**
    * 矿工子账户下拉列表
    *
    * @description
    * @author shangbin
    * @date 2021/8/5 13:58
    * @param [filBillMethodBO]
    * @return java.util.List<com.mei.hui.miner.feign.vo.FilBillSubAccountVO>
    * @version v1.4.1
    */
    public List<FilBillSubAccountVO> selectFilBillSubAccountList(FilBillMethodBO filBillMethodBO);

    /**
    * 分页查询账单消息列表
    *
    * @description
    * @author shangbin
    * @date 2021/8/5 18:59
    * @param [filBillMethodBO]
    * @return java.util.List<com.mei.hui.miner.feign.vo.FilBillVO>
    * @version v1.4.1
    */
    public IPage<FilBillVO> selectFilBillPage(FilBillMethodBO filBillMethodBO);

    /**
     * 查询账单汇总信息
     * @param filBillMethodBO
     * @return
     */
    public BillTotalVO selectFilBillTotal(FilBillMethodBO filBillMethodBO);

    /**
    * 保存上报FIL币账单
    *
    * @description
    * @author shangbin
    * @date 2021/8/16 15:14
    * @param [filBillReportBO]
    * @return void
    * @version v1.4.1
    */
    public void reportBillMq(FilBillReportBO filBillReportBO, List<FilBill> filBillList, List<FilBillTransactions> filBillTransactionsList, FilBillDayAggArgsVO filBillDayAggArgsVO);

    /**
    * 在FIL币账单消息详情表里手动插入一条区块奖励数据
    *
    * @description
    * @author shangbin
    * @date 2021/8/18 14:43
    * @param [filBlockAwardReportBO]
    * @return void
    * @version v1.4.1
    */
    public void insertFilBillBlockAward(FilBlockAwardReportBO filBlockAwardReportBO,FilBillDayAggArgsVO filBillDayAggArgsVO);

    /**
    * 分页查询日账单列表
    *
    * @description
    * @author shangbin
    * @date 2021/8/18 15:50
    * @param [filBillMonthBO]
    * @return com.baomidou.mybatisplus.core.metadata.IPage<com.mei.hui.miner.feign.vo.FilBillDayAggVO>
    * @version v1.4.1
    */
    public IPage<FilBillDayAggVO> selectFilBillDayAggPage(FilBillMonthBO filBillMonthBO);

    /**
     * 账单月汇总
     * @param filBillMonthBO
     * @return
     */
    public BillTotalVO selectFilBillmonthAgg(FilBillMonthBO filBillMonthBO);

    /**
     * 分页查询日账单详情列表
     * @param filBillMonthBO
     * @return
     */
    public IPage<FilBillVO> selectFilBillTransactionsPage(FilBillMonthBO filBillMonthBO);

    /**
     * 新增FIL币账单消息每天汇总表
     * @param minerId
     * @param startDate
     * @param endDate
     * @return
     */
    public Integer insertFilBillDayAgg(String minerId, String startDate, String endDate,LocalDate date);

    /**
     * 账单总汇总-从矿工创建开始至今所有收入以及支出的汇总
     * @param filBillMonthBO
     * @return
     */
    public BillTotalVO selectFilBillAllAgg(FilBillMonthBO filBillMonthBO);

    /**
     * 更新或者插入所有的 FIL币账单消息每天汇总表
     * @param minerId
     * @param date
     * @param inMoney
     * @param outMoney
     * @param balance
     * @param inTransfer
     * @param inBlockAward
     * @param outTransfer
     * @param outNodeFee
     * @param outBurnFee
     * @param outOther
     */
    public void insertOrUpdateFilBillDayAggByMinerIdAndDateAll(String minerId, LocalDateTime dateTime, FilBillDayAggArgsVO filBillDayAggArgsVO);


    /**
     * 批量保存FIL币账单消息详情表、FIL币账单转账信息表，实时计算FIL币账单消息每天汇总表数据
     * @param minerId
     * @param dateTime
     * @param filBillList
     * @param allFilBillTransactionsList
     */
    public void saveBatchReportBillMq(String minerId, LocalDateTime dateTime, List<FilBill> filBillList, List<FilBillTransactions> allFilBillTransactionsList,FilBillDayAggArgsVO filBillDayAggArgsVO);

    /**
     * 账单补录数据
     * @param miner
     * @param date
     * @param balance
     * @param filBillList
     * @param filBillTransactionsList
     * @param filBillDayAggArgsVO
     */
    public void backTrackingBill(String minerId, String date, BigDecimal balance, FilBillDayAgg filBillDayAgg);

    /**
     * 根据minerId、月份分页查询月转入、转出、区块奖励列表
     * @param filBillMonthBO
     * @return
     */
    public IPage<FilBillVO> selectFilBillMonthTransferPage(FilBillMonthBO filBillMonthBO);

    /**
     * 补录账单所有的业务逻辑
     * @param minerId
     * @param todayDate
     * @param balance
     */
    public void reportBillBackTracking(String minerId, String todayDate, BigDecimal balance);

    List<ExcelFilBill> findFilBillMonthTransfer(String minerId,LocalDateTime startDate,LocalDateTime endDate,Integer transferType);
}
