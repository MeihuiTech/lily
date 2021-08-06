package com.mei.hui.user.feign.fallBackFactory;

import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.*;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

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
            public Result<List<SysUserOut>> findAllUser() {
                return null;
            }

            @Override
            public PageResult<SysUserOut> findAllAdminUser(BasePage page) {
                return null;
            }

            @Override
            public Result<SysUserOut> getLoginUser() {
                return null;
            }

            @Override
            public Result<List<FindSysUsersByNameVO>> findSysUsersByName(FindSysUsersByNameBO req) {
                return null;
            }

            @Override
            public Result authority(String token, String url) {
                return null;
            }

            @Override
            public Result<Long> findUserIdByApiKey(String apiKey) {
                return null;
            }
        };
    }
}
