package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mei.hui.miner.entity.QiniuStoreConfig;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.FilMinerControlBalanceMapper;
import com.mei.hui.miner.mapper.SysMachineInfoMapper;
import com.mei.hui.miner.service.*;
import com.mei.hui.util.BigDecimalUtil;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GeneralServiceImpl implements GeneralService {
    //获取七牛集群剩余磁盘命令
    private static String availDiskSizeUrl = "sum by(cluster)(kodo_qbs_blkmaster_physical_space_avail_bytes{service=\"blkmaster\"})";
    @Autowired
    private QiniuStoreConfigService qiniuStoreConfigService;
    @Autowired
    private DiskService diskService;
    @Autowired
    private ISysMinerInfoService sysMinerInfoService;
    @Autowired
    private SysMachineInfoMapper sysMachineInfoMapper;
    @Autowired
    private ISysAggPowerHourService aggPowerHourService;
    @Autowired
    private FilMinerControlBalanceMapper minerControlBalanceMapper;

    public Result<List<FindDiskSizeInfoBO>> findDiskSizeInfo(){
        List<FindDiskSizeInfoBO> list = new ArrayList<>();
        Set<QiniuStoreConfig> qiniuClusters = qiniuStoreConfigService.findQiniuClusters(null);
        log.info("七牛集群列表:{}",JSON.toJSONString(qiniuClusters));

        Map<String, BigDecimal> maps = new HashMap<>();
        for(QiniuStoreConfig qiniuStoreConfig : qiniuClusters){
            BigDecimal availDiskSize = diskService.getDiskSize(qiniuStoreConfig,availDiskSizeUrl);
            log.info("磁盘剩余可用容量:{}",availDiskSize);
            FindDiskSizeInfoBO bo = new FindDiskSizeInfoBO();
            bo.setClusterName(qiniuStoreConfig.getClusterName()+"(剩余)");
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

    /**
     * 获取集群带宽
     * @return
     */
    public Result<List<ClusterBroadbandBO>> findClusterBroadband(){
        List<ClusterBroadbandBO> list = new ArrayList<>();
        Set<QiniuStoreConfig> qiniuClusters = qiniuStoreConfigService.findQiniuClusters(null);
        log.info("七牛集群列表:{}",JSON.toJSONString(qiniuClusters));
        for(QiniuStoreConfig config : qiniuClusters){
            BroadbandVO broadband = diskService.broadband(config,true);
            ClusterBroadbandBO bo = new ClusterBroadbandBO().setClusterName(config.getClusterName())
                    .setBroadbandDownVOList(broadband.getBroadbandDownVOList()).setBroadbandUpVOList(broadband.getBroadbandUpVOList());
            list.add(bo);
        }
        return Result.success(list);
    }

    public Result<List<AvailablePowerVO>> availablePower(){
        List<SysMinerInfo> list = sysMinerInfoService.list();
        List<AvailablePowerVO> lt = list.stream().map(v -> {
            AvailablePowerVO vo = new AvailablePowerVO().setMinerId(v.getMinerId()).setPowerAvailable(v.getPowerAvailable());
            return vo;
        }).collect(Collectors.toList());
        return Result.success(lt);
    }

    public Result<PlatformBaseInfoVO> platformBaseInfo(){
        List<SysMinerInfo> list = sysMinerInfoService.list();
        BigDecimal totalAccount = new BigDecimal("0");
        BigDecimal allPowerAvailable = new BigDecimal("0");
        long totalBlocks = 0L;
        for(SysMinerInfo miner : list){
            totalAccount = totalAccount.add(miner.getBalanceMinerAccount());
            allPowerAvailable = allPowerAvailable.add(miner.getPowerAvailable());
            totalBlocks = totalBlocks + miner.getTotalBlocks();
        }
        //在线设备
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("online",1);
        int count = sysMachineInfoMapper.selectCount(queryWrapper);
        log.info("在线设备:{}",count);

        //24小时出块
        String startDate = DateUtils.lDTYesterdayBeforeLocalDateTimeHour();
        String endDate = DateUtils.lDTBeforeBeforeLocalDateTimeHour();
        QueryWrapper query = new QueryWrapper();
        query.select("coalesce(sum(blocks_per_day),0) as twentyFourBlocks");
        query.ge("date",startDate);
        query.le("date",endDate);
        Map map = aggPowerHourService.getMap(query);
        BigDecimal twentyFourBlocks = (BigDecimal) map.get("twentyFourBlocks");
        log.info("24小时出块数:{}",twentyFourBlocks);
        PlatformBaseInfoVO vo = new PlatformBaseInfoVO()
                .setAllMinerCount(list.size())
                .setTotalAccount(BigDecimalUtil.formatFour(totalAccount))
                .setAllPowerAvailable(allPowerAvailable)
                .setTotalBlocks(totalBlocks)
                .setTwentyFourBlocks(twentyFourBlocks.longValue())
                .setMachineOnlineNum(count);
        return Result.success(vo);
    }

    public Result<List<AccountInfoVO>> accountInfo(){
        //查询post账户余额信息
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("coalesce(sum(balance),0) as balance,miner_id");
        queryWrapper.groupBy("miner_id");
        List<Map<String, Object>> maps = minerControlBalanceMapper.selectMaps(queryWrapper);

        Map<String,BigDecimal> map = new HashMap<>();
        maps.stream().forEach(v->{
            String minerId = (String) v.get("miner_id");
            BigDecimal balance = (BigDecimal) v.get("balance");
            map.put(minerId,balance);
        });
        log.info("post余额信息:{}",JSON.toJSONString(map));

        //查询可用余额、worker账户余额
        List<SysMinerInfo> list = sysMinerInfoService.list();
        List<AccountInfoVO> lt = list.stream().map(v -> {
            BigDecimal balancePostAccount = map.get(v.getMinerId());
            AccountInfoVO vo = new AccountInfoVO()
                    .setBalanceMinerAvailable(BigDecimalUtil.formatFour(v.getBalanceMinerAvailable()))
                    .setBalanceWorkerAccount(BigDecimalUtil.formatFour(v.getBalanceWorkerAccount()))
                    .setBalancePostAccount(BigDecimalUtil.formatFour(balancePostAccount))
                    .setMinerId(v.getMinerId());
            return vo;
        }).collect(Collectors.toList());
        return Result.success(lt);
    }
}
