package com.mei.hui.miner.feign.feignClient;

import com.mei.hui.miner.feign.fallBackFactory.MinerFeignFallbackFactory;
import com.mei.hui.miner.feign.vo.FindUserRateBO;
import com.mei.hui.miner.feign.vo.FindUserRateVO;
import com.mei.hui.miner.feign.vo.SaveFeeRateBO;
import com.mei.hui.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(name = "miner-server",path = "/rate",fallbackFactory = MinerFeignFallbackFactory.class )
public interface CurrencyRateFeign {

    @PostMapping("/saveOrUpdateFeeRate")
    Result saveOrUpdateFeeRate(@RequestBody SaveFeeRateBO saveFeeRateBO);

    @PostMapping("/findUserRate")
    Result<List<FindUserRateVO>> findUserRate(@RequestBody FindUserRateBO findUserRateBO);

    /**
    * 根据userIdList 查询userId和费率的map
    *
    * @description
    * @author shangbin
    * @date 2021/6/7 19:32
    * @param [userIdList, type]
    * @return com.mei.hui.util.Result<java.util.Map<java.lang.Long,java.math.BigDecimal>>
    * @version v1.0.0
    */
    @PostMapping("/getUserIdRateMapByUserIdList")
    public Result<Map<Long,BigDecimal>> getUserIdRateMapByUserIdList(@RequestParam("userIdList") List<Long> userIdList, @RequestParam("type") String type);
}
