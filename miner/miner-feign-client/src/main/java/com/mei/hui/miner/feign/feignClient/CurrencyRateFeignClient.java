package com.mei.hui.miner.feign.feignClient;

import com.mei.hui.miner.feign.fallBackFactory.MinerFeignFallbackFactory;
import com.mei.hui.miner.feign.vo.SaveFeeRateBO;
import com.mei.hui.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "miner-server",path = "/rate",fallbackFactory = MinerFeignFallbackFactory.class )
public interface CurrencyRateFeignClient {

    @PostMapping("/saveOrUpdateFeeRate")
    Result saveOrUpdateFeeRate(@RequestBody SaveFeeRateBO saveFeeRateBO);
}
