package com.mei.hui.miner.feign.feignClient;

import com.mei.hui.miner.feign.fallBackFactory.MinerFeignFallbackFactory;
import com.mei.hui.miner.feign.vo.FindUserRateBO;
import com.mei.hui.miner.feign.vo.FindUserRateVO;
import com.mei.hui.miner.feign.vo.SaveFeeRateBO;
import com.mei.hui.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "miner-server",path = "/rate",fallbackFactory = MinerFeignFallbackFactory.class )
public interface CurrencyRateFeign {

    @PostMapping("/saveOrUpdateFeeRate")
    Result saveOrUpdateFeeRate(@RequestBody SaveFeeRateBO saveFeeRateBO);

    @PostMapping("/findUserRate")
    Result<List<FindUserRateVO>> findUserRate(@RequestBody FindUserRateBO findUserRateBO);
}
