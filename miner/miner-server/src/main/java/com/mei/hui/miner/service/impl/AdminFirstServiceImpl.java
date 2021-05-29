package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.model.PowerAvailableFilVO;
import com.mei.hui.miner.model.SysMinerInfoVO;
import com.mei.hui.miner.service.AdminFirstService;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.BigDecimalUtil;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AdminFirstServiceImpl implements AdminFirstService {

    @Autowired
    private SysMinerInfoMapper sysMinerInfoMapper;


    @Override
    public Long selectAllBlocksPerDay() {
        return sysMinerInfoMapper.selectAllBlocksPerDay();
    }

    @Override
    public BigDecimal selectAllBalanceMinerAccount() {
        return sysMinerInfoMapper.selectAllBalanceMinerAccount();
    }

    @Override
    public BigDecimal selectAllPowerAvailable() {
        return sysMinerInfoMapper.selectAllPowerAvailable();
    }

    @Override
    public Long selectAllMinerIdCount() {
        return sysMinerInfoMapper.selectAllMinerIdCount();
    }

    @Override
    public Map<String,Object> powerAvailablePage(String yesterDayDate,BasePage basePage) {
        // 管理员首页-旷工统计数据-平台有效算力
        BigDecimal allPowerAvailable = selectAllPowerAvailable();
        Page<PowerAvailableFilVO> powerAvailableFilVOPage = new Page<>(basePage.getPageNum(),basePage.getPageSize());
        IPage<PowerAvailableFilVO> result = sysMinerInfoMapper.powerAvailablePage(powerAvailableFilVOPage,yesterDayDate,allPowerAvailable);
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",powerAvailableFilVOPage.getRecords());
        map.put("total",powerAvailableFilVOPage.getTotal());
        return map;
    }
}
