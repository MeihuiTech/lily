package com.mei.hui.miner.service.impl;

import com.mei.hui.miner.entity.PoolInfo;
import com.mei.hui.miner.mapper.PoolInfoMapper;
import com.mei.hui.miner.service.IPoolInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PoolInfoServiceImpl implements IPoolInfoService {

    @Autowired
    private PoolInfoMapper poolInfoMapper;

    @Override
    public PoolInfo selectPoolInfoByUserId(Long userId) {
        PoolInfo info = poolInfoMapper.selectMinerInfoByUserId(userId);

        PoolInfo machineInfo = poolInfoMapper.selectMachineInfoByUserId(userId);
        if (machineInfo != null) {
            info.setWorkerCount(machineInfo.getWorkerCount());
        }

        return info;
    }
}
