package com.mei.hui.miner.service;

import com.mei.hui.miner.feign.vo.FindUserRateBO;
import com.mei.hui.miner.feign.vo.FindUserRateVO;
import com.mei.hui.miner.model.SaveFeeRateBO;
import com.mei.hui.util.Result;

import java.util.List;

public interface CurrencyRateService {

    /**
     * 保存币种费率
     * @param saveFeeRateBO
     * @return
     */
    Result saveFeeRate(SaveFeeRateBO saveFeeRateBO);

    /**
     * 获取用户的币种费率
     * @param findUserRateBO
     * @return
     */
    Result<List<FindUserRateVO>> findUserRate(FindUserRateBO findUserRateBO);
}
