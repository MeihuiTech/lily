package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.entity.ChiaMiner;
import com.mei.hui.miner.mapper.ChiaMinerMapper;
import com.mei.hui.miner.model.SysMinerInfoBO;
import com.mei.hui.miner.service.IChiaMinerService;
import com.mei.hui.util.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ChiaMinerServiceImpl implements IChiaMinerService {
    @Autowired
    private ChiaMinerMapper chiaMinerMapper;
    /**
     * 获取 起亚币 旷工列表
     * @param sysMinerInfoBO
     * @return
     */
    @Override
    public Map<String,Object> findChiaMinerPage(SysMinerInfoBO sysMinerInfoBO)
    {
        LambdaQueryWrapper<ChiaMiner> query = new LambdaQueryWrapper();
        query.eq(ChiaMiner::getUserId, HttpRequestUtil.getUserId());
        query.eq(ChiaMiner::getMinerId,sysMinerInfoBO.getMinerId());
        if("powerAvailable".equals(sysMinerInfoBO.getCloumName())){
            if(sysMinerInfoBO.isAsc()){
                query.orderByAsc(ChiaMiner::getPowerAvailable);
            }else{
                query.orderByDesc(ChiaMiner::getPowerAvailable);
            }
        }else if("balanceMinerAccount".equals(sysMinerInfoBO.getCloumName())){
            if(sysMinerInfoBO.isAsc()){
                query.orderByAsc(ChiaMiner::getBalanceMinerAccount);
            }else{
                query.orderByDesc(ChiaMiner::getBalanceMinerAccount);
            }
        }else if("totalBlockAward".equals(sysMinerInfoBO.getCloumName())){
            if(sysMinerInfoBO.isAsc()){
                query.orderByAsc(ChiaMiner::getTotalBlockAward);
            }else{
                query.orderByDesc(ChiaMiner::getTotalBlockAward);
            }
        }
        IPage<ChiaMiner> page = chiaMinerMapper.selectPage(new Page<>(sysMinerInfoBO.getPageNum(), sysMinerInfoBO.getPageSize()), query);
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",page.getRecords());
        map.put("total",page.getTotal());
        return map;
    }

    /**
     * chia管理员首页-旷工统计数据-平台总资产
     * @return
     */
    @Override
    public BigDecimal selectFilAllBalanceMinerAccount() {
        return chiaMinerMapper.selectAllBalanceMinerAccount();
    }

    /**
     * chia管理员首页-旷工统计数据-平台有效算力
     * @return
     */
    @Override
    public BigDecimal selectFilAllPowerAvailable() {
        return chiaMinerMapper.selectAllPowerAvailable();
    }

    /**
     * chia管理员首页-旷工统计数据-活跃旷工
     * @return
     */
    @Override
    public Long selectFilAllMinerIdCount() {
        return chiaMinerMapper.selectAllMinerIdCount();
    }

    /**
     * chia管理员首页-旷工统计数据-当天出块份数
     * @return
     */
    @Override
    public Long selectFilAllBlocksPerDay(String yesterDayDate) {
        return chiaMinerMapper.selectAllBlocksPerDay(yesterDayDate);
    }


}
