package com.mei.hui.miner.feign.fallBackFactory;

import com.mei.hui.miner.feign.feignClient.AggChiaMinerFeign;
import com.mei.hui.miner.feign.feignClient.AggMinerFeignClient;
import com.mei.hui.miner.feign.vo.AggMinerVO;
import com.mei.hui.miner.feign.vo.UserMinerBO;
import com.mei.hui.util.Result;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AggChiaMinerFeignFallbackFactory implements FallbackFactory<AggChiaMinerFeign> {
    @Override
    public AggChiaMinerFeign create(Throwable throwable) {
        log.error("远程接口异常:",throwable);
        AggChiaMinerFeign aggChiaMinerFeign = new AggChiaMinerFeign(){
            @Override
            public Result<List<AggMinerVO>> findBatchChiaMinerByUserId(UserMinerBO userMinerBO) {
                return null;
            }
        };
        return aggChiaMinerFeign;
    }
}
