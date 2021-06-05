package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.CurrencyRate;
import com.mei.hui.miner.mapper.CurrencyRateMapper;
import com.mei.hui.miner.model.SaveFeeRateBO;
import com.mei.hui.miner.service.CurrencyRateService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CurrencyRateServiceImpl implements CurrencyRateService {

    @Autowired
    private CurrencyRateMapper currencyRateMapper;
    @Autowired
    private UserFeignClient userFeignClient;

    @Transactional(rollbackFor = Exception.class)
    public Result saveFeeRate(SaveFeeRateBO saveFeeRateBO){
        SysUserOut sysUserOut = new SysUserOut();
        sysUserOut.setUserId(saveFeeRateBO.getUserId());
        log.info("查询用户信息,入参：{}", JSON.toJSONString(sysUserOut));
        Result<SysUserOut> userResult = userFeignClient.getUserById(sysUserOut);
        log.info("查询用户信息,出参：{}", JSON.toJSONString(userResult));
        if(!ErrorCode.MYB_000000.getCode().equals(userResult.getCode())){
            throw MyException.fail(userResult.getCode(),userResult.getMsg());
        }
        if(StringUtils.checkValNull(userResult.getData())){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"查询用户为空");
        }

        List<CurrencyRate> currencyRates = saveFeeRateBO.getRats().stream().map(v -> {
            CurrencyRate currencyRate = new CurrencyRate();
            currencyRate.setUserId(saveFeeRateBO.getUserId());
            currencyRate.setType(v.getType());
            currencyRate.setFeeRate(new BigDecimal(v.getFeeRate()));
            currencyRate.setCreateTime(LocalDateTime.now());
            currencyRate.setUpdateTime(LocalDateTime.now());
            return currencyRate;
        }).collect(Collectors.toList());
        /**
         * 更新费率数据：有数据更新，否则新增
         */
        currencyRates.stream().forEach(v->{
            LambdaQueryWrapper<CurrencyRate> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(CurrencyRate::getType,v.getType());
            queryWrapper.eq(CurrencyRate::getUserId,v.getUserId());
            List<CurrencyRate> rate = currencyRateMapper.selectList(queryWrapper);
            if(rate.size() > 0){
                v.setId(rate.get(0).getId());
                currencyRateMapper.updateById(v);
            }else{
                currencyRateMapper.insert(v);
            }
        });
        return Result.OK;
    }


}
