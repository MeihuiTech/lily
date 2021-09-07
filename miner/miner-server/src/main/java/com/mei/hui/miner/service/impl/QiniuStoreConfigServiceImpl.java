package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.QiniuStoreConfig;
import com.mei.hui.miner.mapper.QiniuStoreConfigMapper;
import com.mei.hui.miner.service.QiniuStoreConfigService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 矿工存储服务配置 服务实现类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-09
 */
@Service
public class QiniuStoreConfigServiceImpl extends ServiceImpl<QiniuStoreConfigMapper, QiniuStoreConfig> implements QiniuStoreConfigService {

    /**
     * 获取七牛集群配置
     * @return
     */
    public Set<QiniuStoreConfig> findQiniuClusters(List<String> minerIds){
        LambdaQueryWrapper<QiniuStoreConfig> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(minerIds != null && minerIds.size() > 0){
            lambdaQueryWrapper.in(QiniuStoreConfig::getMinerId,minerIds);
        }
        List<QiniuStoreConfig> list = this.list(lambdaQueryWrapper);
        Set<QiniuStoreConfig> set = new HashSet<>();
        list.stream().forEach(v->set.add(v));
        return set;
    }

}
