package com.mei.hui.miner.feign.fallBackFactory;

import com.mei.hui.miner.feign.feignClient.MinerFeignClient;
import com.mei.hui.miner.feign.vo.FindCodeByUserIdInput;
import com.mei.hui.miner.feign.vo.SysVerifyCodeInput;
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
        MinerFeignClient minerFeignClient = new MinerFeignClient() {
            @Override
            public Result<Integer> CheckConnectionListAll() {
                return null;
            }

//            @Override
//            public Result<SysVerifyCodeInput> findCodeByUserId(FindCodeByUserIdInput input) {
//                return null;
//            }

//            @Override
//            public Result insertSysVerifyCode(SysVerifyCodeInput input) {
//                return null;
//            }
        };
        return minerFeignClient;
    }
}
