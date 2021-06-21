package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.Currency;
import com.mei.hui.miner.entity.CurrencyRate;
import com.mei.hui.miner.feign.vo.FindUserRateBO;
import com.mei.hui.miner.feign.vo.FindUserRateVO;
import com.mei.hui.miner.manager.UserManager;
import com.mei.hui.miner.mapper.CurrencyRateMapper;
import com.mei.hui.miner.mapper.SysCurrencyMapper;
import com.mei.hui.miner.model.SaveFeeRateBO;
import com.mei.hui.miner.service.CurrencyRateService;
import com.mei.hui.miner.service.ISysCurrencyService;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CurrencyRateServiceImpl extends ServiceImpl<CurrencyRateMapper,CurrencyRate> implements CurrencyRateService {

    @Autowired
    private CurrencyRateMapper currencyRateMapper;
    @Autowired
    private UserManager userManager;
    @Autowired
    private ISysCurrencyService iSysCurrencyService;

    @Transactional
    public Result saveFeeRate(SaveFeeRateBO saveFeeRateBO){
        //校验用户是否存在
        userManager.checkUserIsExist(saveFeeRateBO.getUserId());
        //查询币种默认值
        Map<String, BigDecimal> rateMap = iSysCurrencyService.getDefaultRate();
        log.info("查询币种费率，结果:{}",JSON.toJSONString(rateMap));
        List<CurrencyRate> currencyRates = saveFeeRateBO.getRats().stream().map(v -> {
            CurrencyRate currencyRate = new CurrencyRate();
            if(v.getFeeRate() <= 0){
                currencyRate.setFeeRate(rateMap.get(v.getType()));
            }
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

    public Result<List<FindUserRateVO>> findUserRate(FindUserRateBO findUserRateBO){
        //校验用户是否存在
        userManager.checkUserIsExist(findUserRateBO.getUserId());
        //如果type为空则获取所有币种的费率，没有绑定的币种，则取费率默认值
        LambdaQueryWrapper<Currency> wrapper = new LambdaQueryWrapper();
        wrapper.eq(Currency::getStatus,1);
        List<Currency> currencyList = iSysCurrencyService.list(wrapper);
        log.info("查询到的所有币种:{}",JSON.toJSONString(currencyList));
        //获取用户币种费率
        LambdaQueryWrapper<CurrencyRate> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(CurrencyRate::getUserId,findUserRateBO.getUserId());
        if(StringUtils.isNotEmpty(findUserRateBO.getType())){
            queryWrapper.eq(CurrencyRate::getType,findUserRateBO.getType());
        }
        List<CurrencyRate> list = currencyRateMapper.selectList(queryWrapper);
        log.info("用户已绑定币种:{}",JSON.toJSONString(list));
        //取消极筛选出用户没有绑定的币种
        List<Currency> userNotExitCurrencyList = currencyList.stream().filter(v ->{
                    boolean flag = list.stream().map(e -> e.getType()).collect(Collectors.toList()).contains(v.getType());
                    return flag ? false : true;
        }).collect(Collectors.toList());
        log.info("用户没有绑定的币种:{}",JSON.toJSONString(userNotExitCurrencyList));
        List<FindUserRateVO> lt = list.stream().map(v -> {
            FindUserRateVO findUserRateVO = new FindUserRateVO();
            findUserRateVO.setUserId(v.getUserId());
            findUserRateVO.setType(v.getType());
            findUserRateVO.setFeeRate(v.getFeeRate());
            return findUserRateVO;
        }).collect(Collectors.toList());

        //用户没有绑定的币种，取默认费率
        userNotExitCurrencyList.stream().forEach(v->{
            FindUserRateVO findUserRateVO = new FindUserRateVO();
            findUserRateVO.setUserId(findUserRateBO.getUserId());
            findUserRateVO.setType(v.getType());
            findUserRateVO.setFeeRate(v.getRate());
            lt.add(findUserRateVO);
        });
        return Result.success(lt);
    }

    /**
     * 获取 fil,rate
     * @param userId
     * @return
     */
    public Map<String,BigDecimal> getUserRateMap(Long userId){
        //获取用户币种费率
        LambdaQueryWrapper<CurrencyRate> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(CurrencyRate::getUserId,userId);
        List<CurrencyRate> list = currencyRateMapper.selectList(queryWrapper);
        Map<String,BigDecimal> map = new HashMap<>();
        list.stream().forEach(v->{
            map.put(v.getType(),v.getFeeRate());
        });
        return map;
    }

    /**
     * 根据userIdList 查询userId和费率的map
     * @param userIdList
     * @return
     */
    @Override
    public Map<Long, BigDecimal> getUserIdRateMapByUserIdList(List<Long> userIdList,String type) {
        QueryWrapper<CurrencyRate> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id",userIdList);
        queryWrapper.eq("type",type);
        List<CurrencyRate> currencyRateList = currencyRateMapper.selectList(queryWrapper);
        Map<Long,BigDecimal> map = new HashMap<>();
        if (currencyRateList != null && currencyRateList.size() > 0) {
            currencyRateList.stream().forEach(v->{
                map.put(v.getUserId(),v.getFeeRate());
            });
        }
        return map;
    }
}
