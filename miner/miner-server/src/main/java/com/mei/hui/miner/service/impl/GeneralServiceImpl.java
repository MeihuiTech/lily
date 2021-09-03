package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.mei.hui.miner.entity.QiniuStoreConfig;
import com.mei.hui.miner.feign.vo.FindDiskSizeInfoBO;
import com.mei.hui.miner.service.DiskService;
import com.mei.hui.miner.service.GeneralService;
import com.mei.hui.miner.service.QiniuStoreConfigService;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class GeneralServiceImpl implements GeneralService {
    //获取七牛集群剩余磁盘命令
    private static String availDiskSizeUrl = "sum by(cluster)(kodo_qbs_blkmaster_physical_space_avail_bytes{service=\"blkmaster\"})";
    @Autowired
    private QiniuStoreConfigService qiniuStoreConfigService;
    @Autowired
    private DiskService diskService;

    public Result<List<FindDiskSizeInfoBO>> findDiskSizeInfo(){
        List<FindDiskSizeInfoBO> list = new ArrayList<>();
        Set<QiniuStoreConfig> qiniuClusters = qiniuStoreConfigService.findQiniuClusters();
        log.info("七牛集群列表:{}",JSON.toJSONString(qiniuClusters));

        Map<String, BigDecimal> maps = new HashMap<>();
        for(QiniuStoreConfig qiniuStoreConfig : qiniuClusters){
            BigDecimal availDiskSize = diskService.getDiskSize(qiniuStoreConfig,availDiskSizeUrl);
            log.info("磁盘剩余可用容量:{}",availDiskSize);
            FindDiskSizeInfoBO bo = new FindDiskSizeInfoBO();
            bo.setClusterName(qiniuStoreConfig.getClusterName());
            bo.setSize(availDiskSize);
            list.add(bo);

            Map<String, BigDecimal> allbucketInfo = diskService.allbucketInfo(qiniuStoreConfig);
            log.info("集群bucket已使用存储:{}",JSON.toJSONString(allbucketInfo));
            maps.putAll(allbucketInfo);
        }
        //查看矿工，并根据矿工的bucket,在maps中获取已使用存储
        List<QiniuStoreConfig> allMiners = qiniuStoreConfigService.list();
        allMiners.stream().forEach(v->{
            BigDecimal size = maps.get(v.getBucket());
            if(size != null){
                FindDiskSizeInfoBO bo = new FindDiskSizeInfoBO().setClusterName(v.getMinerId()).setSize(size);
                list.add(bo);
            }
        });
        return Result.success(list);
    }
}
