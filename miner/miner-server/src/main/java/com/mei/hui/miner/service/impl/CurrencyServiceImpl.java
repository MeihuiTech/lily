package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.common.enums.EnumCurrencyStatus;
import com.mei.hui.miner.entity.Currency;
import com.mei.hui.miner.entity.CurrencyRate;
import com.mei.hui.miner.feign.vo.AddCurrencyBO;
import com.mei.hui.miner.mapper.CurrencyRateMapper;
import com.mei.hui.miner.mapper.SysCurrencyMapper;
import com.mei.hui.miner.model.SysCurrencyVO;
import com.mei.hui.miner.service.CurrencyRateService;
import com.mei.hui.miner.service.ISysCurrencyService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 币种表
 * @author shangbin
 * @version v1.0.0
 * @date
 **/
@Slf4j
@Service
public class CurrencyServiceImpl extends ServiceImpl<SysCurrencyMapper, Currency> implements ISysCurrencyService {

    @Autowired
    private SysCurrencyMapper sysCurrencyMapper;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private CurrencyRateService currencyRateService;

    /**
    * 不分页排序查询币种列表
    */
    @Override
    public List<SysCurrencyVO> listCurrency() {
        LambdaQueryWrapper<Currency> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Currency::getStatus,EnumCurrencyStatus.available.getStatus());
        List<Currency> sysCurrencyList = sysCurrencyMapper.selectList(queryWrapper);
        return sysCurrencyList.stream().map(v->{
            SysCurrencyVO vo = new SysCurrencyVO();
            vo.setId(v.getId());
            vo.setName(v.getName());
            vo.setType(v.getType());
            vo.setRate(v.getRate());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 不分页查询币种表里的所有可用的币种
     * @return
     */
    @Override
    public List<Currency> allCurrencyList() {
        Currency currency = new Currency();
        currency.setStatus(1);
        QueryWrapper<Currency> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(currency);
        return sysCurrencyMapper.selectList(queryWrapper);
    }


    @Override
    public Map<String,BigDecimal> getDefaultRate(){
        List<Currency> list = allCurrencyList();
        Map<String,BigDecimal> map = new HashMap<>();
        list.stream().forEach(v->{
            map.put(v.getName(),v.getRate());
        });
        return map;
    }

    /**
     * 根据币种type查询并返回name
     * @param type
     * @return
     */
    @Override
    public String getCurrencyNameByType(String type){
        Currency currency = new Currency();
        currency.setType(type);
        QueryWrapper<Currency> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(currency);
        List<Currency> currencyList = sysCurrencyMapper.selectList(queryWrapper);
        if (currencyList != null && currencyList.size() > 0) {
            return currencyList.get(0).getName();
        }
        return null;
    }

    /**
     * 新增币种，业务：
     *   第一步：币种表，新增币种数据
     *   第二步：在用户和币种费率关联表，给所有用户都和新币种绑定关联
     * @param addCurrencyBO
     * @return
     */
    public Result addCurrency(AddCurrencyBO addCurrencyBO){
        /**
         * 币种存在则不新增
         */
        LambdaQueryWrapper<Currency> query = new LambdaQueryWrapper<>();
        query.eq(Currency::getName,addCurrencyBO.getName());
        List<Currency> list = sysCurrencyMapper.selectList(query);


        /**
         * 添加新币种
         */
        Currency currency = new Currency();
        if(list.size() > 0){
            currency = list.get(0);
        }else{
            currency.setName(addCurrencyBO.getName());
            currency.setType(addCurrencyBO.getType());
            currency.setRate(addCurrencyBO.getRate());
            currency.setCreateTime(LocalDateTime.now());
            currency.setUpdateTime(LocalDateTime.now());
            sysCurrencyMapper.insert(currency);
        }
        /**
         * 查询所有用户，user_currency_rate表进行用户和币种费率的绑定
         */
        Result<List<SysUserOut>> allUser = userFeignClient.findAllUser();
        if(!ErrorCode.MYB_000000.getCode().equals(allUser.getCode())){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"获取平台所有用户失败");
        }
        Currency finalCurrency = currency;
        List<CurrencyRate> userCurrencyRate = allUser.getData().stream().map(v -> {
            CurrencyRate currencyRate = new CurrencyRate();
            currencyRate.setFeeRate(finalCurrency.getRate());
            currencyRate.setUserId(v.getUserId());
            currencyRate.setType(finalCurrency.getName());
            currencyRate.setCreateTime(LocalDateTime.now());
            currencyRate.setUpdateTime(LocalDateTime.now());
            return currencyRate;
        }).collect(Collectors.toList());
        //批量插入
        currencyRateService.saveBatch(userCurrencyRate);
        return Result.OK;
    }
}
