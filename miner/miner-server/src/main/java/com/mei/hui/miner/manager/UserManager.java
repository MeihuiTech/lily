package com.mei.hui.miner.manager;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserManager {
    @Autowired
    private UserFeignClient userFeignClient;

    public void checkUserIsExist(Long userId){
        SysUserOut sysUserOut = new SysUserOut();
        sysUserOut.setUserId(userId);
        log.info("查询用户信息,入参：{}", JSON.toJSONString(sysUserOut));
        Result<SysUserOut> userResult = userFeignClient.getUserById(sysUserOut);
        log.info("查询用户信息,出参：{}", JSON.toJSONString(userResult));
        if(!ErrorCode.MYB_000000.getCode().equals(userResult.getCode())){
            throw MyException.fail(userResult.getCode(),userResult.getMsg());
        }
        if(StringUtils.checkValNull(userResult.getData())){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"查询用户为空");
        }
    }
}
