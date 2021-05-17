package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mei.hui.miner.entity.PoolInfo;
import com.mei.hui.miner.entity.SysMachineInfo;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.mapper.PoolInfoMapper;
import com.mei.hui.miner.mapper.SysMachineInfoMapper;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.service.IPoolInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PoolInfoServiceImpl implements IPoolInfoService {

    @Autowired
    private  SysMinerInfoMapper sysMinerInfoMapper;
    @Autowired
    private SysMachineInfoMapper sysMachineInfoMapper;


    @Override
    public PoolInfo selectPoolInfoByUserId(Long userId) {
        /**
         * 查询旷工
         */
        LambdaQueryWrapper<SysMinerInfo> queryWrapper = new LambdaQueryWrapper();
        if(userId != 1){
            queryWrapper.eq(SysMinerInfo::getUserId,userId);
        }
        List<SysMinerInfo> miners = sysMinerInfoMapper.selectList(queryWrapper);
        /**
         * 查询矿机
         */
        List<SysMachineInfo> machines = new ArrayList<>();
        if(miners.size() > 0){
            List<String> minerIds = miners.stream().map(v -> v.getMinerId()).collect(Collectors.toList());
            LambdaQueryWrapper<SysMachineInfo> query = new LambdaQueryWrapper();
            query.in(SysMachineInfo::getMinerId,minerIds);
            machines = sysMachineInfoMapper.selectList(query);
        }
        PoolInfo poolInfo = new PoolInfo();
        poolInfo.setMinerCount(Long.valueOf(miners.size()));
        poolInfo.setWorkerCount(Long.valueOf(machines.size()));
        return poolInfo;
    }
}
