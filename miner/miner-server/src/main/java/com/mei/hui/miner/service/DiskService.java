package com.mei.hui.miner.service;

import com.mei.hui.miner.entity.QiniuStoreConfig;
import com.mei.hui.miner.feign.vo.DiskBO;
import com.mei.hui.miner.feign.vo.DiskVO;
import com.mei.hui.util.Result;

public interface DiskService {

    Result<DiskVO> diskSizeInfo(DiskBO diskBO);

    String getQiNiuToken(QiniuStoreConfig storeConfig);
}
