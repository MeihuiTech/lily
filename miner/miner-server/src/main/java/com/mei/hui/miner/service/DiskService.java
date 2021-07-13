package com.mei.hui.miner.service;

import com.mei.hui.miner.entity.QiniuStoreConfig;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.BroadbandVO;
import com.mei.hui.miner.feign.vo.DiskBO;
import com.mei.hui.miner.feign.vo.DiskVO;
import com.mei.hui.util.Result;

import java.util.List;

public interface DiskService {

    Result<DiskVO> diskSizeInfo(List<SysMinerInfo> sysMinerInfoList);

    String getQiNiuToken(QiniuStoreConfig storeConfig);

    /**
    * 获取宽带信息
    *
    * @description
    * @author shangbin
    * @date 2021/7/9 18:01
    * @param [diskBO]
    * @return com.mei.hui.util.Result<com.mei.hui.miner.feign.vo.DiskVO>
    * @version v1.4.1
    */
    public Result<BroadbandVO> broadband(List<SysMinerInfo> sysMinerInfoList);
}
