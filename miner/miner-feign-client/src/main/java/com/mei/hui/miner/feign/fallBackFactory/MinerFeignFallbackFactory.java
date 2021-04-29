package com.mei.hui.miner.feign.fallBackFactory;

import com.mei.hui.miner.feign.feignClient.MinerFeignClient;
import com.mei.hui.util.Result;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
*@Description:
*@Author: 鲍红建
*@date: 2021/1/6
*/
@Component
@Slf4j
public class MinerFeignFallbackFactory implements FallbackFactory<MinerFeignClient> {
    @Override
    public MinerFeignClient create(Throwable throwable) {
        log.error("远程接口异常:",throwable);
        return new MinerFeignClient() {
            @Override
            public Result getMiner() {
                return null;
            }
        };
    }
}
