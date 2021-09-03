package com.mei.hui.miner.service;

import com.mei.hui.miner.entity.QiniuStoreConfig;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.util.Result;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DiskService {



    /**
    * 获取七牛云集群硬盘容量和宽带信息
    *
    * @description
    * @author shangbin
    * @date 2021/7/13 19:16
    * @param [sysMinerInfoList]
    * @return com.mei.hui.util.Result<com.mei.hui.miner.feign.vo.BroadbandVO>
    * @version v1.4.1
    */
    public List<QiniuVO> selectDiskSizeAndBroadbandList(List<SysMinerInfo> sysMinerInfoList);

    BigDecimal getDiskSize(QiniuStoreConfig qiniuStoreConfig, String metric);

    /**
     * 获取集群所有bucket已使用存储量
     * @return
     */
    Map<String,BigDecimal> allbucketInfo(QiniuStoreConfig qiniuStoreConfig);
}
