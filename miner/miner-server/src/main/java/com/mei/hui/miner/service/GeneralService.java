package com.mei.hui.miner.service;

import com.mei.hui.miner.feign.vo.AvailablePowerVO;
import com.mei.hui.miner.feign.vo.ClusterBroadbandBO;
import com.mei.hui.miner.feign.vo.FindDiskSizeInfoBO;
import com.mei.hui.miner.feign.vo.PlatformBaseInfoVO;
import com.mei.hui.util.Result;

import java.util.List;

public interface GeneralService {
    Result<List<FindDiskSizeInfoBO>> findDiskSizeInfo();

    Result<List<ClusterBroadbandBO>> findClusterBroadband();

    Result<List<AvailablePowerVO>> availablePower();

    Result<PlatformBaseInfoVO> platformBaseInfo();
}
