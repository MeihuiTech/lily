package com.mei.hui.miner.service.impl;

import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.service.AdminFirstService;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
}
