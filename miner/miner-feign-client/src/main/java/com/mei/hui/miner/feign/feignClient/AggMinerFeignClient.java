package com.mei.hui.miner.feign.feignClient;

import com.mei.hui.miner.feign.fallBackFactory.AggMinerFeignFallbackFactory;
import com.mei.hui.miner.feign.fallBackFactory.MinerFeignFallbackFactory;
import com.mei.hui.miner.feign.vo.AggMinerVO;
import com.mei.hui.miner.feign.vo.UserMinerBO;
import com.mei.hui.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "miner-server",path = "/system/miner",fallbackFactory = AggMinerFeignFallbackFactory.class )
public interface AggMinerFeignClient {


}
