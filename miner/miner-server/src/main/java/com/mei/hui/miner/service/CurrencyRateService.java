package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.CurrencyRate;
import com.mei.hui.miner.feign.vo.FindUserRateBO;
import com.mei.hui.miner.feign.vo.FindUserRateVO;
import com.mei.hui.miner.model.SaveFeeRateBO;
import com.mei.hui.util.Result;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CurrencyRateService extends IService<CurrencyRate> {

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

    /**
     * 获取用户的费率 map
     * @param userId
     * @return
     */
    Map<String, BigDecimal> getUserRateMap(Long userId);
}
