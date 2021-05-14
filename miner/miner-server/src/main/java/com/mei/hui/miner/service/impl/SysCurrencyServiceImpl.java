package com.mei.hui.miner.service.impl;

import com.mei.hui.miner.entity.SysCurrency;
import com.mei.hui.miner.mapper.SysCurrencyMapper;
import com.mei.hui.miner.model.SysCurrencyVO;
import com.mei.hui.miner.service.ISysCurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 币种表
 * @author shangbin
 * @version v1.0.0
 * @date
 **/
@Slf4j
@Service
public class SysCurrencyServiceImpl implements ISysCurrencyService {

    @Autowired
    private SysCurrencyMapper sysCurrencyMapper;

    /**
    * 不分页排序查询币种列表
    *
    * @description
    * @author shangbin
    * @date 2021/5/14 14:58
    * @param []
    * @return java.util.List<com.mei.hui.miner.model.SysCurrencyVO>
    * @version v1.0.0
    */
    @Override
    public List<SysCurrencyVO> listCurrency() {
        SysCurrency sysCurrency = new SysCurrency();
        sysCurrency.setDelFlag(false);
        // TODO 一会需要修改：


//        sysCurrencyMapper.
        return null;
    }
}
