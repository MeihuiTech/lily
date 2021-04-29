package com.mei.hui.miner.mapper;

import com.mei.hui.miner.entity.PoolInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PoolInfoMapper {
     PoolInfo selectMinerInfoByUserId(Long userId);
     PoolInfo selectMachineInfoByUserId(Long userId);

     PoolInfo selectMachineInfoByUserIdAndMinerId(@Param("userId") Long userId, @Param("minerId") String minerId);
}
