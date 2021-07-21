package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.feign.vo.FilBillDetailBO;
import com.mei.hui.miner.feign.vo.FilBillDetailVO;
import com.mei.hui.miner.feign.vo.FilBillPageListBO;
import com.mei.hui.miner.feign.vo.FilBillPageListVO;
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

    PageResult<FilBillPageListVO> pageList(FilBillPageListBO bo);

    Result<List<FilBillDetailVO>> detail(FilBillDetailBO bo);
}
