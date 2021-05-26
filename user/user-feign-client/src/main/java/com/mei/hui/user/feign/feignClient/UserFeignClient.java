package com.mei.hui.user.feign.feignClient;

import com.mei.hui.user.feign.fallBackFactory.UserFeignFallbackFactory;
import com.mei.hui.user.feign.vo.*;
import com.mei.hui.util.Result;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
*@Description:
*@Author: 鲍红建
*@date: 2021/1/6
*/
@FeignClient(name = "user-server",path = "/system/user",fallbackFactory = UserFeignFallbackFactory.class )
public interface UserFeignClient {

    /**
     *
     * @param sysUserOut
     * @return
     */
    @PostMapping("/getUserById")
    Result<SysUserOut> getUserById(@RequestBody SysUserOut sysUserOut);

    /**
     * 批量获取用户
     * @param req
     * @return
     */
    @RequestMapping(value = "/findSysUserList",method = RequestMethod.POST)
    Result<List<SysUserOut>> findSysUserList(@RequestBody  FindSysUserListInput req);

    /**
     * 获取当前登陆用户
     * @return
     */
    @PostMapping("/getLoginUser")
     Result<SysUserOut> getLoginUser();
    /**
     * 用户信息模糊查询
     * @param req
     * @return
     */
    @PostMapping("/findSysUsersByName")
    Result<List<FindSysUsersByNameVO>> findSysUsersByName(@RequestBody FindSysUsersByNameBO req);

    @PostMapping("/authority")
    Result authority(@RequestBody String token);

    /**
    * 根据apiKey查询用户的userId
    *
    * @description
    * @author shangbin
    * @date 2021/5/26 11:18
    * @param [apiKey]
    * @return com.mei.hui.util.Result<java.lang.String>
    * @version v1.0.0
    */
    @PostMapping("/findUserIdByApiKey")
    Result<Long> findUserIdByApiKey(@RequestParam("apiKey") String apiKey);

}
