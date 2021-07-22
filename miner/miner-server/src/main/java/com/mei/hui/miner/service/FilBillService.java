package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import org.springframework.web.bind.annotation.RequestBody;

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

    Result<List<FilBillDetailVO>> detail(FilBillDetailBO bo);
}
