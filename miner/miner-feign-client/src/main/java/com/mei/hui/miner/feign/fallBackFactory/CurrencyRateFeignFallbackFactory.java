package com.mei.hui.miner.feign.fallBackFactory;

import com.mei.hui.miner.feign.feignClient.CurrencyRateFeign;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.util.Result;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CurrencyRateFeignFallbackFactory implements FallbackFactory<CurrencyRateFeign> {
    @Override
    public CurrencyRateFeign create(Throwable throwable) {
        log.error("远程接口异常:",throwable);
        CurrencyRateFeign currencyRateFeign = new CurrencyRateFeign() {

            @Override
            public Result saveOrUpdateFeeRate(SaveFeeRateBO saveFeeRateBO) {
                return null;
            }

            @Override
            public Result<List<FindUserRateVO>> findUserRate(FindUserRateBO findUserRateBO) {
                return null;
            }
        };
        return currencyRateFeign;
    }
}
