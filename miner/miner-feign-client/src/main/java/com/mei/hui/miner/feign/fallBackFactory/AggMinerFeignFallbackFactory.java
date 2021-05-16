package com.mei.hui.miner.feign.fallBackFactory;

import com.mei.hui.miner.feign.feignClient.AggMinerFeignClient;
import com.mei.hui.miner.feign.feignClient.MinerFeignClient;
import com.mei.hui.miner.feign.vo.AggMinerVO;
import com.mei.hui.util.Result;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AggMinerFeignFallbackFactory implements FallbackFactory<AggMinerFeignClient> {
    @Override
    public AggMinerFeignClient create(Throwable throwable) {
        log.error("远程接口异常:",throwable);
        AggMinerFeignClient aggMinerFeignClient = new AggMinerFeignClient(){

            @Override
            public Result<List<AggMinerVO>> findBatchMinerByUserId(List<Long> userIds) {
                return null;
            }
        };
        return aggMinerFeignClient;
    }
}
