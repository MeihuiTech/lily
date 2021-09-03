package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.QiniuStoreConfig;

import java.util.Set;

/**
 * <p>
 * 矿工存储服务配置 服务类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-09
 */
public interface QiniuStoreConfigService extends IService<QiniuStoreConfig> {

    /**
     * 获取七牛集群配置
     * @return
     */
    Set<QiniuStoreConfig> findQiniuClusters();
}
