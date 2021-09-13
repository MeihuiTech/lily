package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.NoPlatformMiner;
import com.mei.hui.util.Result;

/**
 * <p>
 * 非平台矿工,仅用于大屏显示 服务类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-09-13
 */
public interface NoPlatformMinerService extends IService<NoPlatformMiner> {
    Result findNoPlatformMiners();
}
