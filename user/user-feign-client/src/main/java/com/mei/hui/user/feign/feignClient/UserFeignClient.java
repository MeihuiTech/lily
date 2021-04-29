package com.mei.hui.user.feign.feignClient;

import com.mei.hui.user.feign.fallBackFactory.UserFeignFallbackFactory;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
*@Description:
*@Author: 鲍红建
*@date: 2021/1/6
*/
@FeignClient(name = "user-server",path = "/system/user",fallbackFactory = UserFeignFallbackFactory.class )
public interface UserFeignClient {

    @RequestMapping("/getSysUser")
    Result<SysUserOut> getSysUser();
}
