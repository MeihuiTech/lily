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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Component
@Slf4j
public class UserManager {
    @Autowired
    private UserFeignClient userFeignClient;

    public void checkUserIsExist(Long userId){
        if(userId == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"userId 不能为空");
        }
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

    /**
     * 获取所有用户
     * @return
     */
    public List<SysUserOut> findAllUser(){
        log.info("userFeignClient:【{}】",userFeignClient);
        Result<List<SysUserOut>> result = userFeignClient.findAllUser();
        if(!ErrorCode.MYB_000000.getCode().equals(result.getCode())){
            throw MyException.fail(result.getCode(),result.getMsg());
        }
        return result.getData();
    }
}
