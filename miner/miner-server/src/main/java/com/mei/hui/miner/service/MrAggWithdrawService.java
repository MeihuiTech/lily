package com.mei.hui.miner.service;

import com.mei.hui.miner.model.AggWithdrawBO;
import com.mei.hui.miner.model.AggWithdrawVO;
import com.mei.hui.util.PageResult;


public interface MrAggWithdrawService {

    /**
     * 用户收益提现汇总分页查询
     * @param sysMinerInfo
     * @return
     */
    PageResult<AggWithdrawVO> pageList(AggWithdrawBO sysMinerInfo);
}
