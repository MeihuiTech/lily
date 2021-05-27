package com.mei.hui.miner.service;


import com.mei.hui.miner.entity.PoolInfo;

public interface IPoolInfoService {
    PoolInfo selectPoolInfoByUserId(Long currencyId);

    PoolInfo getXchMinerAmount(Long currencyId);

}
