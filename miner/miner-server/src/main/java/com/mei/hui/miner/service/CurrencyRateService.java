package com.mei.hui.miner.service;

import com.mei.hui.miner.model.SaveFeeRateBO;
import com.mei.hui.util.Result;

public interface CurrencyRateService {

    /**
     * 保存币种费率
     * @param saveFeeRateBO
     * @return
     */
    Result saveFeeRate(SaveFeeRateBO saveFeeRateBO);
}
