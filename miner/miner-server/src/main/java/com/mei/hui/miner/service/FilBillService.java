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
}
