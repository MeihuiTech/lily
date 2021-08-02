package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.feign.vo.BillAggVO;
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
    * 上报FIL币账单
    *
    * @description
    * @author shangbin
    * @date 2021/7/30 19:23
    * @param [filBillReportBOList]
    * @return java.lang.Integer
    * @version v1.4.1
    */
//    public Integer reportFilBill(List<FilBillReportBO> filBillReportBOList);
}
