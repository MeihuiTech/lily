package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.util.Result;

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
    public void reportBillMq(FilBillReportBO filBillReportBO);

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
    public void insertFilBillBlockAward(FilBlockAwardReportBO filBlockAwardReportBO);

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
}
