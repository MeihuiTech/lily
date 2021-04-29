package com.mei.hui.miner.feign.feignClient;

import com.mei.hui.miner.feign.fallBackFactory.MinerFeignFallbackFactory;
import com.mei.hui.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
*@Description:
*@Author: 鲍红建
*@date: 2021/1/6
*/
@FeignClient(name = "miner-server",path = "/miner",fallbackFactory = MinerFeignFallbackFactory.class )
public interface MinerFeignClient {

    @RequestMapping("/getMiner")
    Result getMiner();
}
