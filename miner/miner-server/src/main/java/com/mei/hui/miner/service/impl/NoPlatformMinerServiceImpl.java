package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.NoPlatformMiner;
import com.mei.hui.miner.feign.vo.NoPlatformAddBO;
import com.mei.hui.miner.feign.vo.NoPlatformBOPage;
import com.mei.hui.miner.feign.vo.NoPlatformVOPage;
import com.mei.hui.miner.mapper.NoPlatformMinerMapper;
import com.mei.hui.miner.service.NoPlatformMinerService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.diff.myers.MyersDiff;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
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

    public Result noPlatformMiner(NoPlatformMiner noPlatformMiner){
        NoPlatformMiner miner = this.getById(noPlatformMiner.getMinerId());
        if(miner == null){
            noPlatformMiner.setCreateTime(LocalDateTime.now());
            this.save(noPlatformMiner);
        }else{
            this.updateById(noPlatformMiner);
        }
        return Result.OK;
    }

    public Result saveOrUpdate(NoPlatformAddBO bo){
        if(StringUtils.isEmpty(bo.getMinerId())){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工id不能为空");
        }
        NoPlatformMiner vo = this.getById(bo.getMinerId());
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
            lambdaUpdateWrapper.set(NoPlatformMiner::getDeviceNum,bo.getDeviceNum());
            lambdaUpdateWrapper.set(NoPlatformMiner::getCreateTime,LocalDateTime.now());
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

}
