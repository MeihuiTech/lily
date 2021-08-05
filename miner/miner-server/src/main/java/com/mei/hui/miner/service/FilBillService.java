package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.feign.vo.BillAggVO;
import com.mei.hui.miner.feign.vo.FilBillMethodBO;
import com.mei.hui.miner.feign.vo.FilBillPageListBO;
import com.mei.hui.miner.feign.vo.FilBillReportBO;
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

    Result<BillAggVO> pageList(FilBillPageListBO bo);

    /**
     * 账单方法下拉列表
     * @param filBillMethodBO
     * @return
     */
    public List<String> selectFilBillMethodList(FilBillMethodBO filBillMethodBO);
}
