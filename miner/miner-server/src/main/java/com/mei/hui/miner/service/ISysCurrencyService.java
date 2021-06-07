package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.Currency;
import com.mei.hui.miner.feign.vo.AddCurrencyBO;
import com.mei.hui.miner.model.SysCurrencyVO;
import com.mei.hui.util.Result;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 币种表
 * @author shangbin
 * @version v1.0.0
 * @date 2021/5/14 14:46
 **/
public interface ISysCurrencyService extends IService<Currency> {


    /**
    * 不分页排序查询币种列表
    *
    * @description
    * @author shangbin
    * @date 2021/5/14 14:57
    * @param []
    * @return java.util.List<com.mei.hui.miner.model.SysCurrencyVO>
    * @version v1.0.0
    */
    public List<SysCurrencyVO> listCurrency();

    /**
    * 不分页查询币种表里的所有可用的币种
    *
    * @description
    * @author shangbin
    * @date 2021/6/3 19:53
    * @param []
    * @return java.util.List<com.mei.hui.miner.entity.Currency>
    * @version v1.0.0
    */
    public List<Currency> allCurrencyList();

    /**
    * 根据币种type查询并返回name
    *
    * @description
    * @author shangbin
    * @date 2021/6/4 14:40
    * @param [type]
    * @return java.lang.String
    * @version v1.0.0
    */
    public String getCurrencyNameByType(String type);

    Map<String, BigDecimal> getDefaultRate();

    Result addCurrency(AddCurrencyBO addCurrencyBO);
}
