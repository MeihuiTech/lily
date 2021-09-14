package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.NoPlatformPerHourAgg;

/**
 * <p>
 * 非平台矿工,每小时出块数 服务类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-09-13
 */
public interface NoPlatformPerHourAggService extends IService<NoPlatformPerHourAgg> {

    Long getPreNoPlatformPerHourAgg(String minerId);
}
