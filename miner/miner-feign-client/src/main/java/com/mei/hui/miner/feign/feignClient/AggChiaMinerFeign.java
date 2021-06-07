package com.mei.hui.miner.feign.feignClient;

import com.mei.hui.miner.feign.fallBackFactory.AggMinerFeignFallbackFactory;
import com.mei.hui.miner.feign.vo.AggMinerVO;
import com.mei.hui.miner.feign.vo.UserMinerBO;
import com.mei.hui.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "miner-server",path = "/chiaMiner",fallbackFactory = AggMinerFeignFallbackFactory.class )
public interface AggChiaMinerFeign {

    @PostMapping(value = "/findBatchChiaMinerByUserId")
    Result<List<AggMinerVO>> findBatchChiaMinerByUserId(@RequestBody UserMinerBO userMinerBO);
}
