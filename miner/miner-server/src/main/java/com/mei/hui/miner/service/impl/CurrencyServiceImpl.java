package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mei.hui.miner.common.enums.EnumCurrencyStatus;
import com.mei.hui.miner.entity.Currency;
import com.mei.hui.miner.mapper.SysCurrencyMapper;
import com.mei.hui.miner.model.SysCurrencyVO;
import com.mei.hui.miner.service.ISysCurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 币种表
 * @author shangbin
 * @version v1.0.0
 * @date
 **/
@Slf4j
@Service
public class CurrencyServiceImpl implements ISysCurrencyService {

    @Autowired
    private SysCurrencyMapper sysCurrencyMapper;

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
}
