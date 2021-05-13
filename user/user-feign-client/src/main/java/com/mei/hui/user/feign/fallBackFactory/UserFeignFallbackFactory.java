package com.mei.hui.user.feign.fallBackFactory;

import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.FindSysUserListInput;
import com.mei.hui.user.feign.vo.FindSysUsersByNameBO;
import com.mei.hui.user.feign.vo.FindSysUsersByNameVO;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.Result;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
*@Description:
*@Author: 鲍红建
*@date: 2021/1/6
*/
@Component
@Slf4j
public class UserFeignFallbackFactory implements FallbackFactory<UserFeignClient> {
    @Override
    public UserFeignClient create(Throwable throwable) {
        log.error("远程接口异常:",throwable);
        return new UserFeignClient() {
            @Override
            public Result<SysUserOut> getUserById(SysUserOut sysUserOut) {
                return null;
            }

            @Override
            public Result<List<SysUserOut>> findSysUserList(FindSysUserListInput req) {
                return null;
            }

            @Override
            public Result<SysUserOut> getLoginUser() {
                return null;
            }

            @Override
            public Result signin(String token) {
                return null;
            }

            @Override
            public Result<List<FindSysUsersByNameVO>> findSysUsersByName(FindSysUsersByNameBO req) {
                return null;
            }
        };
    }
}
