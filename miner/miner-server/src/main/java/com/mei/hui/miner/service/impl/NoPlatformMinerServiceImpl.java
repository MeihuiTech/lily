package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.NoPlatformMiner;
import com.mei.hui.miner.entity.NoPlatformPerHourAgg;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.NoPlatformMinerMapper;
import com.mei.hui.miner.service.NoPlatformMinerService;
import com.mei.hui.miner.service.NoPlatformPerHourAggService;
import com.mei.hui.util.BigDecimalUtil;
import com.mei.hui.util.MyException;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.diff.myers.MyersDiff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 非平台矿工,仅用于大屏显示 服务实现类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-09-13
 */
@Slf4j
@Service
public class NoPlatformMinerServiceImpl extends ServiceImpl<NoPlatformMinerMapper, NoPlatformMiner> implements NoPlatformMinerService {
    @Autowired
    private NoPlatformPerHourAggService noPlatformPerHourAggService;

    public Result findNoPlatformMiners(){
        LambdaQueryWrapper<NoPlatformMiner> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(NoPlatformMiner::getStatus,0);
        List<String> minerIds = this.list(lambdaQueryWrapper).stream().map(v -> v.getMinerId()).collect(Collectors.toList());
        return Result.success(minerIds);
    }

    public Result noPlatformMiner(NoPlatformMiner noPlatformMiner){
        LambdaQueryWrapper<NoPlatformMiner> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(NoPlatformMiner::getStatus,0);
        lambdaQueryWrapper.eq(NoPlatformMiner::getMinerId,noPlatformMiner.getMinerId());
        NoPlatformMiner miner = this.getOne(lambdaQueryWrapper);
        log.info("查询矿工是否存在:{}",JSON.toJSONString(miner));
        if(miner == null){
            noPlatformMiner.setCreateTime(LocalDateTime.now());
            this.save(noPlatformMiner);
        }else{
            noPlatformMiner.setId(miner.getId());
            this.updateById(noPlatformMiner);
        }
        return Result.OK;
    }

    public Result saveOrUpdate(NoPlatformAddBO bo){
        if(StringUtils.isEmpty(bo.getMinerId())){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工id不能为空");
        }
        LambdaQueryWrapper<NoPlatformMiner> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(NoPlatformMiner::getMinerId,bo.getMinerId());
        lambdaQueryWrapper.eq(NoPlatformMiner::getStatus,0);
        NoPlatformMiner vo = this.getOne(lambdaQueryWrapper);
        log.info("查询非平台矿工是否已经存在:{}",JSON.toJSONString(vo));
        if(vo == null){
            NoPlatformMiner noPlatformMiner = new NoPlatformMiner()
                    .setMinerId(bo.getMinerId())
                    .setDeviceNum(bo.getDeviceNum())
                    .setCreateTime(LocalDateTime.now())
                    .setUpdateTime(LocalDateTime.now());
            this.save(noPlatformMiner);
        }else {
            LambdaUpdateWrapper<NoPlatformMiner> lambdaUpdateWrapper = new LambdaUpdateWrapper();
            lambdaUpdateWrapper.eq(NoPlatformMiner::getMinerId,bo.getMinerId());
            lambdaUpdateWrapper.eq(NoPlatformMiner::getStatus,0);
            lambdaUpdateWrapper.set(NoPlatformMiner::getDeviceNum,bo.getDeviceNum());
            lambdaUpdateWrapper.set(NoPlatformMiner::getUpdateTime,LocalDateTime.now());
            this.update(lambdaUpdateWrapper);
        }
        return Result.OK;
    }

    public Result delete(String minerId){
        LambdaUpdateWrapper<NoPlatformMiner> lambdaUpdateWrapper = new LambdaUpdateWrapper();
        lambdaUpdateWrapper.eq(NoPlatformMiner::getMinerId,minerId);
        lambdaUpdateWrapper.set(NoPlatformMiner::getStatus,1);
        lambdaUpdateWrapper.set(NoPlatformMiner::getUpdateTime,LocalDateTime.now());
        this.update(lambdaUpdateWrapper);
        return Result.OK;
    }

    public PageResult<NoPlatformVOPage> pageList(NoPlatformBOPage bo){
        QueryWrapper<NoPlatformMiner> queryWrapper = new QueryWrapper();
        queryWrapper.eq("status","0");
        IPage<NoPlatformMiner> page = this.page(new Page<>(bo.getPageNum(), bo.getPageSize()), queryWrapper);
        List<NoPlatformVOPage> list = page.getRecords().stream().map(v -> {
            NoPlatformVOPage vo = new NoPlatformVOPage()
                    .setDeviceNum(v.getDeviceNum())
                    .setMinerId(v.getMinerId())
                    .setType(v.getType());
            return vo;
        }).collect(Collectors.toList());
        PageResult result = new PageResult(page.getTotal(),list);
        return result;
    }

    /**
     * 将非平台矿工的累计出块，有效算力 添加到大屏中
     * @param vo
     */
    public void setPlatformBaseInfo(PlatformBaseInfoVO vo){
        if(vo != null){
            //计算非平台矿工数据
            LambdaQueryWrapper<NoPlatformMiner> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(NoPlatformMiner::getStatus,0);
            queryWrapper.eq(NoPlatformMiner::getType,1);
            List<NoPlatformMiner> miners = this.list(queryWrapper);
            log.info("非平台矿工:{}", JSON.toJSONString(miners));
            for(NoPlatformMiner miner : miners){
                vo.setTotalBlocks(vo.getTotalBlocks()+miner.getTotalBlocks());
                vo.setTotalAccount(vo.getTotalAccount().add(new BigDecimal(miner.getBalanceMinerAccount())));
                vo.setAllPowerAvailable(vo.getAllPowerAvailable().add(miner.getPowerAvailable()));
                vo.setMachineOnlineNum(vo.getMachineOnlineNum() + miner.getDeviceNum());
            }
            vo.setAllMinerCount(vo.getAllMinerCount() + miners.size());
            //24小时出块数
            if(miners.size() > 0){
                List<String> minerIds = miners.stream().map(v -> v.getMinerId()).collect(Collectors.toList());
                LocalDateTime dateTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
                QueryWrapper<NoPlatformPerHourAgg> wrapper = new QueryWrapper();
                wrapper.select("coalesce(sum(per_hour_blocks),0) as perHourBlocks");
                wrapper.in("miner_id",minerIds);
                wrapper.le("create_time",dateTime);
                wrapper.ge("create_time",dateTime.minusHours(24));
                Map<String, Object> map = noPlatformPerHourAggService.getMap(wrapper);
                BigDecimal perHourBlocks = (BigDecimal) map.get("perHourBlocks");
                log.info("非平台矿工24小时出块数:{}",perHourBlocks);
                vo.setTwentyFourBlocks(vo.getTwentyFourBlocks() + perHourBlocks.longValue());
            }
        }
        vo.setTotalAccount(BigDecimalUtil.formatFour(vo.getTotalAccount()));
    }

    /**
     * 将非平台矿工的有效算力值当做磁盘显示在大屏中
     */
    public void setFindDiskSizeInfo(List<FindDiskSizeInfoBO> list){
        //计算非平台矿工数据
        LambdaQueryWrapper<NoPlatformMiner> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(NoPlatformMiner::getStatus,0);
        queryWrapper.eq(NoPlatformMiner::getType,1);
        List<NoPlatformMiner> miners = this.list(queryWrapper);
        List<FindDiskSizeInfoBO> lt = miners.stream().map(v -> {
            FindDiskSizeInfoBO bo = new FindDiskSizeInfoBO()
                    .setSize(v.getPowerAvailable())
                    .setClusterName(v.getMinerId());
            return bo;
        }).collect(Collectors.toList());
        list.addAll(lt);
    }

    public void setAvailablePower(List<AvailablePowerVO> list){
        //计算非平台矿工数据
        LambdaQueryWrapper<NoPlatformMiner> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(NoPlatformMiner::getStatus,0);
        queryWrapper.eq(NoPlatformMiner::getType,1);
        List<NoPlatformMiner> miners = this.list(queryWrapper);
        List<AvailablePowerVO> lt = miners.stream().map(v -> {
            AvailablePowerVO bo = new AvailablePowerVO()
                    .setMinerId(v.getMinerId())
                    .setPowerAvailable(v.getPowerAvailable());
            return bo;
        }).collect(Collectors.toList());
        list.addAll(lt);
    }

    public void setAccountInfo(List<AccountInfoVO> list){
        //计算非平台矿工数据
        LambdaQueryWrapper<NoPlatformMiner> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(NoPlatformMiner::getStatus,0);
        queryWrapper.eq(NoPlatformMiner::getType,1);
        List<NoPlatformMiner> miners = this.list(queryWrapper);
        List<AccountInfoVO> lt = miners.stream().map(v -> {
            AccountInfoVO bo = new AccountInfoVO()
                    .setMinerId(v.getMinerId())
                    .setBalanceMinerAvailable(BigDecimalUtil.formatFour(new BigDecimal(v.getBalanceMinerAvailable())))
                    .setBalancePostAccount(BigDecimalUtil.formatFour(new BigDecimal(v.getPostBalance())))
                    .setBalanceWorkerAccount(BigDecimalUtil.formatFour(new BigDecimal(v.getWorkerBalance())));
            return bo;
        }).collect(Collectors.toList());
        list.addAll(lt);
    }

}
