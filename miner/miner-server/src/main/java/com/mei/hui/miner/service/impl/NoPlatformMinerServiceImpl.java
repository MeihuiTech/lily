package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.NoPlatformMiner;
import com.mei.hui.miner.mapper.NoPlatformMinerMapper;
import com.mei.hui.miner.service.NoPlatformMinerService;
import com.mei.hui.util.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 非平台矿工,仅用于大屏显示 服务实现类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-09-13
 */
@Service
public class NoPlatformMinerServiceImpl extends ServiceImpl<NoPlatformMinerMapper, NoPlatformMiner> implements NoPlatformMinerService {

    public Result findNoPlatformMiners(){
        LambdaQueryWrapper<NoPlatformMiner> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(NoPlatformMiner::getStatus,0);
        List<String> minerIds = this.list(lambdaQueryWrapper).stream().map(v -> v.getMinerId()).collect(Collectors.toList());
        return Result.success(minerIds);
    }

}
