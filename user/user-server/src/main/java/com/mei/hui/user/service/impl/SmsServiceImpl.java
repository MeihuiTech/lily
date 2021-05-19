package com.mei.hui.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.config.smsConfig.SmsUtil;
import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.mapper.SysUserMapper;
import com.mei.hui.user.model.SmsSendBO;
import com.mei.hui.user.service.SmsService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import com.mei.hui.util.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SmsUtil smsUtil;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 1、先判断是否已经发送过验证码，如果上次发送的验证码还有效则给提示
     * 2、发送验证码后 保存在redis 1分钟
     * @param smsSendBO
     * @return
     */
    public Result send(SmsSendBO smsSendBO){
        Long userId = HttpRequestUtil.getUserId();
        //查看用户1分钟之内是否应发送过验证码
        String code = redisUtil.get(String.format(SystemConstants.SMSKKEY,smsSendBO.getServiceName(),userId));
        if(StringUtils.isNotEmpty(code)){
            throw MyException.fail(UserError.MYB_333333.getCode(),"验证码已经发送");
        }
        //生成6位验证码
        int min = 123456;
        int max = 999999;
        Random r = new Random();
        code = String.valueOf(r.nextInt(max - min + 1) + min);
        //查询用户信息
        log.info("查询用户信息,入参:userId = {}",userId);
        SysUser user = sysUserMapper.selectById(userId);
        log.info("查询用户信息,出参:{}", JSON.toJSONString(user));
        log.info("发送短信,phonenumber={},code={}",user.getPhonenumber(),code);
        boolean smsResult = smsUtil.send(user.getPhonenumber(), code);
        //发送验证码成功，则存入redis
        if(smsResult){
            //缓存保留验证码1分钟
            redisUtil.set(smsSendBO.getServiceName()+"_"+userId,code,1, TimeUnit.MINUTES);
        }
        return Result.OK;
    }
}
